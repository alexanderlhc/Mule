# README

## Guide

### Windows

Download the `Mule-windows.zip` file. 
The size is large because it must carry the texLive compiler.

* Extract zip
* Run `Mule.jar` (NOTE the `texLive` directory must be next to `Mule.jar`
* Rest should work out

### Mac/Linux

* Install latexmk [read howto here](https://mg.readthedocs.io/latexmk.html)
* Extract zip
* Run `Mule.jar`
* Rest should work out

## TODO

- [ ] Paths are an absolutle mess. Get it sorted!
- [x] Better UNICODE support + author and title must be allowed spaces.
- [ ] Update log during compile (hard: see section further down)
- [ ] More languages added + confirm color palette
- [ ] Pass binary for Mac and Linux

## Updating log during latex compil

* Tried passing propertyListener (with onChangeListener) and updating insde the compiler loop.
* Passing TextArea to same loop.

Both doesn't update during execution (even though System out works) and updates after.

## Ideas

- Options become gradually avail
- Run becomes available when all options filled properly. Recheck (for source files) just checks if files exist in LW. 
	- All options passing gets green
- When terminal object "dies" then execute remove temp. on startup remove temp if exists.
- Deal with errors/exceptions in popup. 
