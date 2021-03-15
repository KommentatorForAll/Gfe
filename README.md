# Gfe
An advanced version of a greenfoot enging

# Greenfoot
[Greenfoot](https://greenfoot.org) is a beginner level gameengine for java. But the Engine itself is quite bad when trying to do advanced stuff, e.g. loading/creating fonts, advanced image operations etc.

# Why?
As stated above, Greenfoot has issues with advanced operations. Therefore, I decided to create my own engine, capable of doing the things stated above.
I decided to make the engine using Threading, and therefore am able to control the tickspeed better. (Greenfoot takes a number between 1 and 100; And at least I have no idea what that means, as 1 is slower as 1 tps and 100 is faster than 100 tps).

# Features, not included in Greenfoot
* Highly controllable tickspeed
* Load fonts
* Load images from outside your project
* Load images in folderstrucures inside of a jar
* Export to jar like one should be able to
* Buttons and similar
* Mouse and Keyboard call events (**May need refactor of your old code**)
* Custom Image and Text positioning on sprites
