OverlappyBird
=============

Flappy Bird Clone made with Overlap2D for learning purposes

--------------------------------------------------

This project requires Overlap2DRuntime to compile, 
Instructions to get it running on eclipse:

Make sure you have Gradle Eclipse Plugin (http://dist.springsource.com/release/TOOLS/gradle)

1) Download this project and import as gardle Project.
2) go to [Overlap2DRuntime github Project](https://github.com/gevorg-kopalyan/Overlap2dRuntime)
3) Click download as ZIP button on the right, andextract it to some folder when downloaded
4) From eclipse click Import project and choose "Gradle Project" 
5) Choose downloaded projects folder as "Root folder" and click Build Model
6) Select the checkbox on appeared project in the list, and click import.
7) Click on overlap2druntime project and choose gradle->refresh just to be on the safe side
8) Right click on over-flappy-bird-core -> Properties -> Java Build Path -> Projects Tab -> Add and choose Overlap2Druntime project.
9) Clear and rebuild everything, and it should work.