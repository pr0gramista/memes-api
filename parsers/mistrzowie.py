from parsel import Selector
from utils import download, find_id_in_url, catch_errors, get_last_part_url
from data import ImageContent, Meme, Author, Page
import re


ROOT = "https://mistrzowie.org"
COMMENT = re.compile(r"Skomentuj\(([0-9]+?)\)")


def scrap(url):
    html = download(url)
    return parse(html)


def parse(html):
    document = Selector(text=html)
    memes = [catch_errors(parse_meme, element) for element in document.css("div.pic")]
    memes = [meme for meme in memes if meme is not None]

    title = document.css("title::text").get()
    next_page_url = "/mistrzowie/page/" + get_last_part_url(
        document.css(".list_next_page_button::attr(href)").get()
    )
    return Page(title, memes, next_page_url)


def parse_meme(m):
    title = m.css("h1.picture > a::text").get()
    if title is None:
        return None

    title = title.strip()
    url = m.css("h1.picture > a::attr(href)").get()

    points = None
    points_text = m.css("span.total_votes_up > span.value::text").get()
    try:
        points = int(points_text)
    except:
        pass

    comment_count = None
    comments_count_text = (
        m.css("a.lcomment::text").get().replace("\t", "").replace("\n", "")
    )

    result = COMMENT.match(comments_count_text)
    if result:
        try:
            comment_count = int(result[1])
        except:
            pass
    else:
        comment_count = 0

    content = None
    src = m.css("img.pic::attr(src)").get()
    if src:
        content = ImageContent(ROOT + src)

    return Meme(
        title,
        ROOT + url,
        "/mistrzowie/{}".format(find_id_in_url(url)),
        content,
        None,
        None,
        points,
        comment_count,
    )
