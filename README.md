# README

![Screenshot](https://i.imgur.com/BKcq0MB.png)

## Guide

Download the `Mule-[OS].zip` from [here](https://github.com/alexanderlhc/Mule/releases/tag/aves.corvidae)

* Extract zip
* Run `Mule.jar` (NOTE the `TinyTeX` directory must be next to `Mule.jar`
* Rest should work out

## TODO

### Must 

- [ ] More languages added + confirm color palette

### Cleaner code

- [ ] Paths are an absolute mess (tmpDir,WorkDir etc..), passed and reread without system now.
- [ ] In Controller method "compileToPdf" it reads and sanitizes all files in selected directory again. Maybe just read from already sanitised ListView?

### Nice to have

- [ ] Update log during compile (hard: see section further down)

## Updating log during compilation

* Tried passing propertyListener (with onChangeListener) and updating inside the compiler loop.
* Passing TextArea to same loop.

Both doesn't update during execution (even though System out works) and updates after.
