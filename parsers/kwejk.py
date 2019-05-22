from parsel import Selector
from utils import download, find_id_in_url, catch_errors, get_last_part_url
from data import ImageContent, GalleryContent, VideoContent, Meme, Author, Tag, Page


def scrap(url):
    html = download(url)
    return parse(html)


def parse(html):
    document = Selector(text=html)
    memes = [
        catch_errors(parse_meme, element)
        for element in document.css("main .media-element")
    ]
    memes = [meme for meme in memes if meme is not None]
    title = document.css("title::text").get()
    next_page_url = "/kwejk/page/" + get_last_part_url(
        document.css(".btn-next::attr(href)").get()
    )
    return Page(title, memes, next_page_url)


def parse_simple_content(html):
    image = html.css("div.figure-holder > figure > a > img::attr(src)").get()
    if image is not None:
        return ImageContent(image)

    video = html.css("div.figure-holder > figure > a > video::attr(src)").get()
    if video is not None:
        return VideoContent(video)

    return None


def parse_gallery(url):
    document = Selector(text=download(url))
    slides = [
        src.replace("_thumb", "")
        for src in document.css(".slider-nav > li > a > img::attr(src)").getall()
    ]
    return GalleryContent(slides)


def parse_meme(m):
    title = m.css(".content > h2 > a::text").get().strip()
    url = m.css(".content > h2 > a::attr(href)").get().strip()

    tags = [
        Tag(tag.css("::text").get(), tag.attrib["href"])
        for tag in m.css(".content > div > div > a")
    ]

    author_link = m.css("div.user-bar > div > a")
    author_url = author_link.attrib["href"]
    author_name = author_link.css("span.name::text").get()

    comment_count_text = m.attrib["data-comments-count"]
    votes_down_text = m.attrib["data-vote-down"]
    votes_up_text = m.attrib["data-vote-up"]

    points = None
    try:
        points = int(votes_up_text) - int(votes_down_text)
    except:
        pass

    comment_count = None
    try:
        comment_count = int(comment_count_text)
    except:
        pass

    content = None
    if "/przegladaj" in url:
        content = parse_gallery(url)
    else:
        content = parse_simple_content(m)

    if content is None:
        return None

    return Meme(
        title,
        url,
        "/kwejk/{}".format(find_id_in_url(url)),
        content,
        Author(author_name, author_url),
        tags,
        points,
        comment_count,
    )
