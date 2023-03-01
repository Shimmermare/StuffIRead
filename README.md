# Stuff I Read

Desktop app for keeping track of stories you read.

## When you may need this

You are reading tons of stories, probably most of them are fanfics.
Excel spreadsheet with all that you read is 10 screens long and searching is a nightmare.
Also, you like to score stories and write reviews, even if only for yourself.
Maybe you also like to re-read stories sometimes.

## Development and support

This app was created for personal usage and as a way to explore Compose framework.
**That means support and further development is not guaranteed.**

## Features

### Review and score stories that you read

TODO

### Browse stories with extensive search

TODO

### Tag system: tag categories, implied tags, search expressions

TODO

### Sequels & prequels

TODO  
Stories can have their sequels/prequels linked for easy navigation

### Local story archive

TODO  
Multiple formats supported: EPUB, HTML, PDF, TXT.

### Import from various story/fanfiction websites

TODO: Currently supported:

- [archiveofourown.org](https://archiveofourown.org/)
- [pastebin.com](https://pastebin.com/)
- [poneb.in](https://poneb.in/)
- [FimFiction.net](https://fimfiction.net/) (MLP)
- [ponepaste.org](https://ponepaste.org/) (MLP)
- [ficbook.net](https://ficbook.net) (Russian)
- [ponyfiction.org](https://ponyfiction.org) (MLP, Russian)

### Export to CSV/Excel

TODO

### Stats and graphs

TODO

## Storage format

Story archive is stored as collection JSON files: one for tags: `tags.json`, and individual directories for stories.

Q: Why use separate files instead of something like SQLite database?  
A: I wanted story archive to be usable with Git/other VCS.

## Attributions
App Icon: [Books free icon by Freepik - Flaticon](https://www.flaticon.com/free-icon/books_4890961)  
Example Story Covers: [Image by Freepik](https://www.freepik.com/free-vector/gradient-abstract-landscape-covers-collection_16133726.htm)  