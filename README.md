# Memes API [![Build Status](https://travis-ci.org/pr0gramista/memes-api.svg?branch=master)](https://travis-ci.org/pr0gramista/memes-api)

API for scrapping common meme sites.
Currently supports:
* [demotywatory.pl](http://demotywatory.pl)
* [kwejk.pl](http://kwejk.pl)
* [mistrzowie.org](http://mistrzowie.org)
* [thecodinglove.com](http://thecodinglove.com)
* [jbzd.pl](https://jbzdy.pl)
* [9gag.com](http://9gag.com)

No sites to be supported, suggest one?

## API
`/`

Response: available sites
```
[
  "/demotywatory",
  "/kwejk",
  "/mistrzowie",
  "/thecodinglove",
  "/jbzd",
  "/9gag"
]
```

Then you can access them by accessing fe. `/kwejk`
```
//shortened response
{
  "title": "KWEJK.pl - Najlepszy zbiór obrazków z Internetu!",
  "memes": [
    {
      "title": "Dziewczyna z piłką",
      "content": {
        "url": "http://i1.kwejk.pl/k/obrazki/2016/11/7479f6497e46508ab0d515fcc7047b72.mp4",
        "contentType": "VIDEO"
      },
      "url": "http://kwejk.pl/obrazek/2814335/dziewczyna-z-pilka.html",
      "commentAmount": 6,
      "points": 125
    },
    {
      "title": "Ma szansę być prezydentem",
      "content": {
        "url": "http://i1.kwejk.pl/k/obrazki/2016/11/09100a20e1dd607694e91f408e15c761.jpg",
        "contentType": "IMAGE"
      },
      "url": "http://kwejk.pl/obrazek/2814963/ma-szanse-byc-prezydentem.html",
      "commentAmount": 6,
      "points": 126
    }
  ],
  "nextPage": "/kwejk/page/31310"
}
```
