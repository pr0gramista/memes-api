from parsers import kwejk, anonimowe, mistrzowie, jbzd, demoty
import jsons
import utils


def test_kwejk_parse(files, snapshot):
    snapshot.assert_match(jsons.dumps(kwejk.parse(files["kwejk.html"])))


def test_anonimowe_parse(files, snapshot):
    snapshot.assert_match(jsons.dumps(anonimowe.parse(files["anonimowe.html"])))


def test_mistrzowie_parse(files, snapshot):
    snapshot.assert_match(jsons.dumps(mistrzowie.parse(files["mistrzowie.html"])))


def test_jbzd_parse(files, snapshot):
    snapshot.assert_match(jsons.dumps(jbzd.parse(files["jbzd.html"])))


def test_demotywatory_parse(files, snapshot, monkeypatch):
    def fake_download(url):
        f = "demot-{}.html".format(utils.get_last_part_url(url))
        if f in files:
            return files[f]
        raise Exception()

    monkeypatch.setattr("parsers.demoty.download", fake_download)

    snapshot.assert_match(jsons.dumps(demoty.parse(files["demotywatory.html"])))
