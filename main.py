import jsons
from parsers import kwejk, jbzd, demoty, mistrzowie, anonimowe, ninegag
from flask import Flask
from flask_cors import CORS

app = Flask(__name__)
CORS(app)


def to_response(page):
    return jsons.dumps(page)


@app.route("/")
def hello():
    return to_response(
        [
            "/kwejk",
            "/jbzd",
            "/9gag",
            "/9gagnsfw",
            "/demotywatory",
            "/mistrzowie",
            "/anonimowe",
        ]
    )


@app.route("/kwejk")
def kwejk_root():
    return to_response(kwejk.scrap("https://kwejk.pl"))


@app.route("/kwejk/page/<page>")
def kwejk_page(page):
    return to_response(kwejk.scrap("https://kwejk.pl/strona/{}".format(page)))


@app.route("/jbzd")
def jbzd_root():
    return to_response(jbzd.scrap("https://jbzdy.pl"))


@app.route("/jbzd/page/<page>")
def jbzd_page(page):
    return to_response(jbzd.scrap("https://jbzdy.pl/strona/{}".format(page)))


@app.route("/mistrzowie")
def mistrzowie_root():
    return to_response(mistrzowie.scrap("http://mistrzowie.org"))


@app.route("/mistrzowie/page/<page>")
def mistrzowie_page(page):
    return to_response(mistrzowie.scrap("http://mistrzowie.org/page/{}".format(page)))


@app.route("/anonimowe")
def anonimowe_root():
    return to_response(anonimowe.scrap("https://anonimowe.pl"))


@app.route("/anonimowe/page/<page>")
def anonimowe_page(page):
    return to_response(anonimowe.scrap("https://anonimowe.pl/{}".format(page)))


@app.route("/demotywatory")
def demotywatory_root():
    return to_response(demoty.scrap("https://m.demotywatory.pl"))


@app.route("/demotywatory/page/<page>")
def demotywatory_page(page):
    return to_response(demoty.scrap("https://m.demotywatory.pl/page/{}".format(page)))


@app.route("/9gag")
def ninegag_root():
    return to_response(
        ninegag.scrap("https://9gag.com/v1/group-posts/group/default/type/hot")
    )


@app.route("/9gag/page/<page>")
def ninegag_page(page):
    return to_response(
        ninegag.scrap(
            "https://9gag.com/v1/group-posts/group/default/type/hot?c=10&after={}".format(
                page
            )
        )
    )


@app.route("/9gagnsfw")
def ninegagnsfw_root():
    return to_response(
        ninegag.scrap("https://9gag.com/v1/group-posts/group/nsfw/type/hot", nsfw=True)
    )


@app.route("/9gagnsfw/page/<page>")
def ninegagnsfw_page(page):
    return to_response(
        ninegag.scrap(
            "https://9gag.com/v1/group-posts/group/nsfw/type/hot?c=10&after={}".format(
                page
            ),
            nsfw=True,
        )
    )


if __name__ == "__main__":
    app.run(debug=True)
