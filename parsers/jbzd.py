from parsel import Selector
from utils import (
    download,
    find_id_in_url,
    remove_big_whitespaces_selector,
    catch_errors,
    get_last_part_url,
)
from data import VideoContent, ImageContent, Meme, Author, Tag, Page


def scrap(url):
    html = download(url)
    return parse(html)


def parse(html):
    document = Selector(text=html)
    memes = [
        catch_errors(parse_meme, element)
        for element in document.css("div.content > section > article.resource-object")
    ]
    memes = [meme for meme in memes if meme is not None]

    title = document.css("title::text").get()
    next_page_url = "/jbzd/page/" + get_last_part_url(
        document.css(".btn-next-page::attr(href)").get()
    )
    return Page(title, memes, next_page_url)


def parse_content(html):
    image = html.css("img.resource-image::attr(src)").get()

    if image is not None:
        return ImageContent(image)

    video = html.css("video::attr(src)").get()
    if video is not None:
        return VideoContent(video)

    return None


def parse_meme(m):
    title = m.css(".title > a::text").get().strip()
    url = m.css(".title > a::attr(href)").get().strip()

    tags = [
        Tag(tag.css("::text").get(), tag.attrib["href"]) for tag in m.css(".tags > a")
    ]

    points = None
    points_text = m.css(".btn-plus span::text").get()
    try:
        points = int(points_text)
    except:
        pass

    comment_count = None
    comments_count_text = (
        remove_big_whitespaces_selector(m.css("span.comments")).css("::text").get()
    )
    try:
        comment_count = int(comments_count_text)
    except:
        pass

    content = parse_content(m.css(".media"))
    if content is None:
        return None

    return Meme(
        title,
        url,
        "/jbzd/{}".format(find_id_in_url(url)),
        content,
        None,
        tags,
        points,
        comment_count,
    )
