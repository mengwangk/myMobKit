# trim-off-newlines [![Build Status](https://travis-ci.org/stevemao/trim-off-newlines.svg?branch=master)](https://travis-ci.org/stevemao/trim-off-newlines)

> Similar to String#trim() but removes only newlines


## Install

```
$ npm install --save trim-off-newlines
```


## Usage

```js
var trimOffNewlines = require('trim-off-newlines');

trimOffNewlines('\n\nunicorns\n\n');
//=> 'nunicorns'
```


## License

MIT © [Steve Mao](https://github.com/stevemao)
