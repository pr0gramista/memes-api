from parsel import Selector
from utils import (
    download,
    remove_big_whitespaces_selector,
    find_id_in_url,
    catch_errors,
)
from data import VideoContent, GalleryContent, ImageContent, Meme, Author
import re


ROOT = "https://m.demotywatory.pl"
GALLERY_IMAGE_SCRIPT = re.compile(r"<img\\n\\tclass=\\\"rsImg \\\"\\n\\tsrc=\\\"(.+?)\.jpg")
GALLERY_VIDEO_SCRIPT = re.compile(r"<source\\n\\t\\t\\tsrc=\\\"(.+?)\.mp4")


def scrap(url):
    html = download(url)
    return parse(html)


def parse(html):
    document = Selector(text=html)
    memes = [
        catch_errors(parse_meme, element) for element in document.css(".demotivator")
    ]
    return [meme for meme in memes if meme is not None]


def parse_gallery(html):
    title = html.css("img::attr(alt)").get()
    url = html.css("a::attr(href)").get()
    slides = []

    gallery_html = download(ROOT + url)
    results = GALLERY_IMAGE_SCRIPT.finditer(gallery_html)
    for result in results:
        slide = result.group(1).replace("\\/", "/") + ".jpg"
        slides = slides + [slide]

    results = GALLERY_VIDEO_SCRIPT.finditer(gallery_html)
    for result in results:
        slide = result.group(1).replace("\\/", "/") + ".mp4"
        slides = slides + [slide]

    return (title, url, GalleryContent(slides), None)


def parse_content(html):
    clazz = html.attrib["class"]

    if "image_gallery" in clazz:
        return parse_gallery(html)
    elif "image" in clazz or "image_gif" in clazz:
        image = html.css("img.demot_pic")
        title = image.attrib["alt"]
        src = image.attrib["src"]
        url = html.css("a::attr(href)").get()

        return (title, url, ImageContent(src), None)
    elif "video_mp4" in clazz:
        src = html.css("source::attr(src)").get()
        title = html.css(".demot_title::text").get()
        description = html.css(".demot_description::text").get()
        url = html.css("a::attr(href)").get()

        return (title, url, VideoContent(src), description)

    return (None, None, None, None)


def parse_meme(m):
    title, url, content, description = parse_content(m)

    if url is None:
        return

    points = None
    points_text = m.css(".up_votes::text").get()
    try:
        points = int(points_text)
    except:
        pass

    comment_count = None
    comments_count_text = m.css(".demot-comments a::text").get()
    try:
        comment_count = int(comments_count_text)
    except:
        pass

    return Meme(
        title,
        ROOT + url,
        "/demotywatory/{}".format(find_id_in_url(url)),
        content,
        None,
        None,
        points,
        comment_count,
    )
