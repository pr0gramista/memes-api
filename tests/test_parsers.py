from parsers import kwejk, anonimowe, mistrzowie, jbzd, demoty
import jsons


def test_kwejk_parse(files, snapshot):
  snapshot.assert_match(jsons.dumps(kwejk.parse(files['kwejk.html'])))


def test_anonimowe_parse(files, snapshot):
  snapshot.assert_match(jsons.dumps(anonimowe.parse(files['anonimowe.html'])))


def test_mistrzowie_parse(files, snapshot):
  snapshot.assert_match(jsons.dumps(mistrzowie.parse(files['mistrzowie.html'])))


def test_jbzd_parse(files, snapshot):
  snapshot.assert_match(jsons.dumps(jbzd.parse(files['jbzd.html'])))


def test_demotywatory_parse(files, snapshot):
  snapshot.assert_match(jsons.dumps(demoty.parse(files['demotywatory.html'])))