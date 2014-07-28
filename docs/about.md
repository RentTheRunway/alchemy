[< back to Readme](../README.md)

#About

- Table of Contents
  * [Contributors](#contributors)
  * [Frequently Asked Questions](#faq)
  * [Release Notes](#release_notes)
  * [Documentation TODOs](#todo)


<a name="contributors"></a>
###Contributors

Many thanks to: _Gene Trog and Carlo Barbara_


<a name="faq"></a>
###Frequently Asked Questions

**_How can I commit to Alchemy?_**
>  Go to the [GitHub project](https://github.com/RentTheRunway2/alchemy), fork it, and submit a pull request. 
   We prefer small, single-purpose pull requests over large, multi-purpose ones. We reserve the right to turn
   down any proposed changes, but in general we're delighted when people want to make our projects better!


<a name="release_notes"></a>
### Release Notes

- v0.1.0
  * Initial release
- v0.1.1
  * Change filtering of experiments from identity type to segments
- v0.1.2
  * Add support for specifying a seed value for randomizing treatment assignment
  * More examples using composite identities
- v0.1.3
  * Made service more extensible
- v0.1.4
  * Allow specifying of credentials for MongoStoreProvider
- v0.1.5
  * Refactored filtering entirely
  * Replaced concept of segments with attributes name/value pairs
  * Allow for filtering on attributes using filtering expressions
  * Allow specifying overrides using the same kind of filtering expressions
- v0.1.6
  * More robust handling of failed experiment loading in MongoStoreProvider
- v0.1.7
  * Expose endpoint for updating treatment description  
- v0.1.8
  * Changed namespaces to io.rtr

<a name="todo"></a>
### Documentation TODOs
