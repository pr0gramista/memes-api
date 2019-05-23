from parsel import Selector
from utils import download, get_last_part_url, catch_errors
from data import VideoContent, ImageContent, Meme, Author, Tag, Page
from json import loads
import html
import re

ROOT = "https://9gag.com"
NEXT_CURSOR = re.compile("after=(.+)&c=(\\d+)")


def scrap(url, nsfw=False):
    data = download(url)
    return parse(data, nsfw=nsfw)


def parse(raw, nsfw=False):
    json = loads(raw)
    posts = json["data"]["posts"]
    memes = [catch_errors(parse_meme, post) for post in posts]
    memes = [meme for meme in memes if meme is not None]

    next_page_url = None
    result = NEXT_CURSOR.match(json["data"]["nextCursor"])
    if result:
        if nsfw is True:
            next_page_url = "/9gagnsfw/page/" + result.group(1)
        else:
            next_page_url = "/9gag/page/" + result.group(1)

    return Page(None, memes, next_page_url)


def parse_meme(m):
    title = html.unescape(m["title"])
    url = m["url"]

    comment_count = int(m["commentsCount"])
    points = int(m["upVoteCount"]) - int(m["downVoteCount"])
    tags = [Tag(tag["key"], ROOT + tag["url"]) for tag in m["tags"]]

    content = None

    # Determine type
    t = m["type"]
    if t == "Animated":
        for k, v in m["images"].items():
            if "duration" in v:
                content = VideoContent(v["url"])
    else:
        src = m["images"]["image700"]["url"]
        content = ImageContent(src)

    return Meme(
        title,
        url,
        "/9gag/{}".format(get_last_part_url(url)),
        content,
        None,
        tags,
        points,
        comment_count,
    )
