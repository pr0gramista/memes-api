from parsel import Selector
from utils import download, remove_big_whitespaces_selector, catch_errors
from data import TextContent, Meme, Author, Page


def scrap(url):
    html = download(url)
    return parse(html)


def parse(html):
    document = Selector(text=html)
    memes = [
        catch_errors(parse_meme, element) for element in document.css("article.story")
    ]
    memes = [meme for meme in memes if meme is not None]
    title = document.css("title::text").get()
    next_page_url = (
        "/anonimowe/page"
        + document.css("nav.pagination > div.next > a::attr(href)").get()
    )
    return Page(title, memes, next_page_url)


def parse_meme(m):
    title = m.css("header.story-header a::text").get()
    url = m.css("header.story-header a::attr(href)").get()

    points = None
    points_text = m.css("span.points::text").get()
    try:
        points = int(points_text)
    except:
        pass

    text = "\n".join(
        [
            part.replace("\r\n", "")
            for part in m.css("article > section ::text").getall()
        ]
    )
    content = TextContent(text)

    return Meme(
        title,
        url,
        "/anonimowe/{}".format(title.replace("#", "")),
        content,
        None,
        None,
        points,
        None,
    )
