# This Is The Way
![alt text](/public/the-mandalorian.jpeg)

## The Mandalorian API

A REST API built using Kotlin and Ktor that provides quotes from The Mandalorian TV series and its spin-off shows, The Book of Boba Fett, and Ahsoka. It also offers information about characters and planets. Additionally, the API includes protected endpoints for administrators to create, edit, and delete quotes, characters, and planets. The API and the Postgres database are running in Docker containers on a Digital Ocean droplet.

## API

Swagger documentation can be found at https://the-mandalorian.dev/swagger

### Characters
```GET /random/character```

Gets a random character.
```
{
    "id": 1,
    "name": "The Mandalorian",
    "description": "The Mandalorian, also known as Mando or Din Djarin, is a bounty hunter navigating the outer rim of the galaxy in the era after the fall of the Galactic Empire. He unexpectedly becomes the guardian of Grogu, a young Force-sensitive being. He forms a strong bond with Grogu, despite his initial reluctance and conflicting Mandalorian beliefs.",
    "imgUrl": "https://static.wikia.nocookie.net/starwars/images/8/8a/DinDjarinSansHelmet-TMCh16.png/revision/latest?cb=20201221013823"
}
```
```GET /character/{name}```

Gets a character by ```{name}``` e.g., ```GET /character/Ahsoka Tano```
```
{
    "id": 6,
    "name": "Ahsoka Tano",
    "description": "Once a young Padawan under Anakin Skywalker, Ahsoka Tano is now a seasoned warrior. She met The Mandalorian and Grogu on Corvus. She guided The Mandalorian towards Grogu's destiny and warned him of the dangers Grogu faces. She teamed up with her apprentice Sabine Wren to find Ezra Bridger and tried to stop the return of Grand Admiral Thrawn.",
    "imgUrl": "https://static.wikia.nocookie.net/starwars/images/2/27/Ahsoka-Tano-AG-2023.png/revision/latest?cb=20231009063143"
}
```
```POST /character```

Creates a new character. Below is an example of a request body.
```
{
    "name":"C-3PO",
    "description": "Loreum Ipsum",
    "imgUrl": "https://static.wikia.nocookie.net/starwars/images/a/a2/C-3PO-TROSTGG.png/revision/latest?cb=20230706042830"
}
```

```PUT /character/{id}```

Edits a character by ```{id}```. E.g., ```PUT /character/58```. Below is an example of a request body.
```
{
    "id": 58,
    "name": "C-3PO",
    "description": "C-3PO is a 3PO-series protocol droid that was rebuilt by Anakin Skywalker. He served Padme Amidala during the Clone Wars and Luke, Leia and Han during the Galatic Civil War and the New Republic era. When Senator Hamato Xiono strongly opposed to Hera Syndulla's actions, rejecting the notion of Grand Admiral Thrawn's return and a rising Imperial Remnant, C-3PO arrived at the trial at the order of Senator Leia Organa, in order to convince the New Republic Senate that Hera had been acting with her approval.",
    "imgUrl": "https://static.wikia.nocookie.net/starwars/images/a/a2/C-3PO-TROSTGG.png/revision/latest?cb=20230706042830"
}
```
```DELETE /character/{id}```

Deletes a character by ```{id}```. E.g., ```DELETE /character/58```

### Quotes
```GET /random/quote```

Gets a random quote.
```
    {
        "id": 1,
        "show": "The Mandalorian",
        "season": 1,
        "episode": "Chapter 1",
        "character": "The Mandalorian",
        "quote": "I can bring you in warm or I can bring you in cold."
    }
```
```GET /quotes/character/{name}```

Gets quotes by character with ```{name}```. E.g., ```GET /quotes/character/The Mandalorian ```
```
[
    {
        "id": 1,
        "show": "The Mandalorian",
        "season": 1,
        "episode": "Chapter 1",
        "character": "The Mandalorian",
        "quote": "I can bring you in warm or I can bring you in cold."
    },
    {
        "id": 2,
        "show": "The Mandalorian",
        "season": 1,
        "episode": "Chapter 1",
        "character": "The Mandalorian",
        "quote": "I like those odds."
    },
    {
        "id": 13,
        "show": "The Mandalorian",
        "season": 1,
        "episode": "Chapter 2",
        "character": "The Mandalorian",
        "quote": "I’m a Mandalorian. Weapons are part of my religion."
    },
  ...
]
```
```GET /quotes/show/{show}```

Gets quotes by ```{show}```. E.g., ```GET /quotes/show/Ahsoka```
```
[
    {
        "id": 320,
        "show": "Ahsoka",
        "season": 1,
        "episode": "Part One",
        "character": "Ahsoka Tano",
        "quote": "Let's just say I didn't follow standard Jedi protocol."
    },
    {
        "id": 321,
        "show": "Ahsoka",
        "season": 1,
        "episode": "Part One",
        "character": "Baylan Skoll",
        "quote": "You're right about one thing, captain. We are no Jedi."
    },
    {
        "id": 322,
        "show": "Ahsoka",
        "season": 1,
        "episode": "Part One",
        "character": "Ahsoka Tano",
        "quote": "Sometimes the right reasons have the wrong consequences."
    },
  ...
]
```
```GET /quotes/show/{show}?season={season}```

Gets quotes by ```{show}``` and ```{season}```. E.g., ```GET /quotes/show/The Mandalorian?season=3```
```
[
    {
        "id": 239,
        "show": "The Mandalorian",
        "season": 3,
        "episode": "Chapter 17",
        "character": "The Armorer",
        "quote": "I shall walk the way of the Mand’alore."
    },
    {
        "id": 240,
        "show": "The Mandalorian",
        "season": 3,
        "episode": "Chapter 17",
        "character": "The Armorer",
        "quote": "You have removed your helmet. What's worse, you did so of your own free will. You are no longer Mandalorian."
    },
    {
        "id": 241,
        "show": "The Mandalorian",
        "season": 3,
        "episode": "Chapter 17",
        "character": "The Mandalorian",
        "quote": "The Creed teaches us of redemption."
    },
 ...
]
```
```POST /quote```

Creates a quote. Below is an example of a request body.
```
{
    "show": "Ahsoka",
    "season": 1,
    "episode": "Part Seven",
    "character": "C-3PO",
    "quote": "Loreum Ipsum"
}
```
```PUT /quote/{id}```

Edits a quote by ```{id}```. E.g., ```PUT /quote/385```. Below is an example of a request body.
```
{
    "id": 385,
    "show": "Ahsoka",
    "season": 1,
    "episode": "Part Seven",
    "character": "C-3PO",
    "quote": "The honoruable Senator Organa has become aware of an unfortunate situation and wishes to present this data transcript which hopes may resolve the matter. As I was about to say, the transcript shows that Senator Organa personally sanctioned General Syndulla's reconnaissance mission to Seatos being incredibly unaware that Senator Xiono had held a vote against such a mission without her."
}
```
```DELETE /quote/{id}```

Deletes a quote by ```{id}```. E.g., ```DELETE /quote/385```

### Planets
```GET /random/planet```

Gets a random planet.
```
{
    "id": 27,
    "name": "Dathomir",
    "description": "Dathomir is a remote planet located in the Dathomir system of the Quelli sector in the Outer Rim Territories. The dark side of the Force has a strong presence on Dathomir and it is home to several witch clans. Grand Admiral Thrawn, his forces, and the Great Mothers escaped to Dathomir from Peridea.",
    "imgUrl": "https://static.wikia.nocookie.net/starwars/images/3/34/DathomirJFO.jpg/revision/latest?cb=20200222032237"
}
```
```GET /planet/{name}```

Gets a planet by ```{name}```. E.g., ```GET /planet/Nevarro```
```
{
    "id": 2,
    "name": "Nevarro",
    "description": "Nevarro is a volcanic planet located in the Outer Rim Territories. It is known for its lava fields and volcanic rivers. Initially, Nevarro was where the Bounty Hunter's guild operated from under Greef Karga. It eventually became a trade outpost.",
    "imgUrl": "https://static.wikia.nocookie.net/starwars/images/5/5c/Nevarro-TMCh17.png/revision/latest?cb=20230423193402"
}
```
```POST /planet```

Creates a planet. Below is an example of a request body.
```
{
    "name":"Naboo",
    "description": "Loreum Ipsum",
    "imgUrl": "https://static.wikia.nocookie.net/starwars/images/f/f0/Naboo_planet.png/revision/latest?cb=20240216051108"
}
```
```PUT /planet/{id}```

Edits a planet by ```{id}```. E.g., ```PUT /planet/28```. Below is an example of a request body.
```
{
    "id": 28,
    "name": "Naboo",
    "description": "Naboo is a planet located in the Chommell sector of the Mid Rim. It is home to the indigenous Gungan species and was the home-world of Padme Amidala.",
    "imgUrl": "https://static.wikia.nocookie.net/starwars/images/f/f0/Naboo_planet.png/revision/latest?cb=20240216051108"
}
```
```DELETE /planet/${id}```

Deletes a planet by ```{id}```. E.g., ```DELETE /planet/28```