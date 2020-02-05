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

### Must 

- [ ] Pass binary for Mac and Linux
- [ ] Better error catching (currently TOO many printStackTrace)
- [ ] More languages added + confirm color palette

### Cleaner code

- [ ] Paths are an absolute mess (tmpDir,WorkDir etc..), passed and reread without system now.
- [ ] In Controller method "compileToPdf" it reads and sanitizes all files in selected directory again. Maybe just read from already sanitised ListView?

### Nice to have

- [ ] Update log during compile (hard: see section further down)

### Cosmetic care for GUI

- [ ] Clearer when fields are invalid and acceptable

## Updating log during compilation

* Tried passing propertyListener (with onChangeListener) and updating inside the compiler loop.
* Passing TextArea to same loop.

Both doesn't update during execution (even though System out works) and updates after.