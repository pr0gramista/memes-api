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


ROOT = "https://m.faktopedia.pl"


def scrap(url):
    html = download(url)
    return parse(html)


def parse(html):
    document = Selector(text=html)
    memes = [
        catch_errors(parse_meme, element)
        for element in document.css(".pic-list div.pictureWrapper")
    ]
    memes = [meme for meme in memes if meme is not None]

    title = document.css("title::text").get()
    next_page_url = "/faktopedia/page/" + get_last_part_url(
        document.css("a.next-page::attr(href)").get()
    )
    return Page(title, memes, next_page_url)


def parse_content(html):
    image = html.css("img.demot_pic")
    title = image.attrib["alt"]
    src = ROOT + image.attrib["src"].replace("//upl", "/upl")
    url = html.css("a::attr(href)").get()

    return (title, url, ImageContent(src), None)


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
        "/faktopedia/{}".format(find_id_in_url(url)),
        content,
        None,
        None,
        points,
        comment_count,
    )
