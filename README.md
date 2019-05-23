# Memes API

API for scrapping common meme sites.
Currently supports:
* [demotywatory.pl](http://demotywatory.pl)
* [kwejk.pl](http://kwejk.pl)
* [mistrzowie.org](http://mistrzowie.org)
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
  "memes": [
    {
      "author": {
        "name": "Camaris",
        "url": "https://kwejk.pl/uzytkownik/camaris"
      },
      "comment_count": 19,
      "content": {
        "src": "https://i1.kwejk.pl/k/obrazki/2019/05/bhkOlFp6BnQhQHYr.jpg",
        "type": "IMAGE"
      },
      "points": 278,
      "tags": [
        {
          "name": "#film",
          "url": "https://kwejk.pl/tag/film"
        },
        {
          "name": "#obrazek",
          "url": "https://kwejk.pl/tag/obrazek"
        }
      ],
      "title": "a zwłaszcza z przyjaciolmi",
      "url": "https://kwejk.pl/obrazek/3388289/a-zwlaszcza-z-przyjaciolmi.html",
      "view_url": "/kwejk/3388289"
    }
  ],
  "next_page_url": "/kwejk/page/40878",
  "title": "Ministerstwo memów, zdjęć i innych śmiesznych obrazków - KWEJK.pl"
}
```
