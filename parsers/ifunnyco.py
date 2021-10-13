from parsel import Selector
from utils import (
    download,
    catch_errors,
    get_last_part_url,
)
from json import loads
from data import VideoContent, ImageContent, Meme, Author, Tag, Page


ROOT = "https://ifunny.co"


def scrap(url, page):
    data = download(
        url,
        additional_headers={
            "Accept": "application/json",
            "x-requested-with": "fetch",
            "x-csrf-token": "229673f83eae692d0702b4b38b417ade",
            "Cookie": "cookie: volume=false; x-csrf-token=229673f83eae692d0702b4b38b417ade; CID=66de99bbaa471b3200e114969e28aa2e775d4b9404d166b080ecc13d7720e441.4e379e8e457e97c6; sound=off; viewMode=list",
        },
    )
    return parse(data, page)


def parse(raw, page):
    json = loads(raw)
    items = json["items"]
    memes = [catch_errors(parse_meme, item) for item in items]
    memes = [meme for meme in memes if meme is not None]
    next_page_url = "/ifunnyco/page/" + str(int(page) + 1)

    return Page(None, memes, next_page_url)


def parse_meme(m):
    title = m["title"]
    url = m["canonical"]

    comment_count = int(m["comments"])
    points = m["smiles"]
    tags = [Tag(tag, ROOT + "/tags/" + tag) for tag in m["tags"]]

    content = None

    author = Author(m["creator"]["nick"], ROOT + m["creator"]["profileUrl"])

    # Determine type
    t = m["type"]
    if t == "video":
        content = VideoContent(m["url"])
    else:
        content = ImageContent(m["url"])

    return Meme(
        title,
        url,
        "/ifunnyco/{}".format(get_last_part_url(url)),
        content,
        author,
        tags,
        points,
        comment_count,
    )
