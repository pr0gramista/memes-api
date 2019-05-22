from parsel import Selector
from utils import (
    download,
    remove_big_whitespaces_selector,
    find_id_in_url,
    catch_errors,
    get_last_part_url,
)
from data import VideoContent, GalleryContent, ImageContent, Meme, Author, Page
import re


ROOT = "https://m.demotywatory.pl"
GALLERY_IMAGE_SCRIPT = re.compile(
    r"<img\\n\\tclass=\\\"rsImg \\\"\\n\\tsrc=\\\"(.+?)\.jpg"
)
GALLERY_VIDEO_SCRIPT = re.compile(r"<source\\n\\t\\t\\tsrc=\\\"(.+?)\.mp4")


def scrap(url):
    html = download(url)
    return parse(html)


def parse(html):
    document = Selector(text=html)
    memes = [
        catch_errors(parse_meme, element) for element in document.css(".demotivator")
    ]
    memes = [meme for meme in memes if meme is not None]

    title = document.css("title::text").get()
    next_page_url = "/demotywatory/page/" + get_last_part_url(
        document.css("a.next-page::attr(href)").get()
    )
    return Page(title, memes, next_page_url)


def parse_gallery(html):
    title = html.css("img::attr(alt)").get()
    url = html.css("a::attr(href)").get()
    slides = []

    gallery_html = download(ROOT + url)
    results = GALLERY_IMAGE_SCRIPT.finditer(gallery_html)
    for result in results:
        slide = result.group(1).replace("\\/", "/").replace("//upl", "/upl") + ".jpg"
        slides = slides + [slide]

    results = GALLERY_VIDEO_SCRIPT.finditer(gallery_html)
    for result in results:
        slide = result.group(1).replace("\\/", "/").replace("//upl", "/upl") + ".mp4"
        slides = slides + [slide]

    return (title, url, GalleryContent(slides), None)


def parse_content(html):
    clazz = html.attrib["class"]

    if "image_gallery" in clazz:
        return parse_gallery(html)
    elif "image" in clazz or "image_gif" in clazz:
        image = html.css("img.demot_pic")
        title = image.attrib["alt"]
        src = image.attrib["src"].replace("//upl", "/upl")
        url = html.css("a::attr(href)").get()

        return (title, url, ImageContent(src), None)
    elif "video_mp4" in clazz:
        src = html.css("source::attr(src)").get().replace("//upl", "/upl")
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
