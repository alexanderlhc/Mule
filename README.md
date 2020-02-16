# README

![Screenshot](https://i.imgur.com/q8fzp6a.png)

## Guide

Download the `Mule-[OS].zip` from [here](https://github.com/alexanderlhc/Mule/releases/tag/aves.corvidae)

* Extract zip
* Run `Mule.jar` (NOTE the `TinyTeX` directory must be next to `Mule.jar`
* Rest should work out

## TODO

### Must 

- [ ] More languages + confirm color palette

### Nice to have

- [ ] Update log during compile (hard: see section further down)

## Updating log during compilation

* Tried passing propertyListener (with onChangeListener) and updating inside the compiler loop.
* Passing TextArea to same loop.

Both doesn't update during execution (even though System out works) and updates after.
