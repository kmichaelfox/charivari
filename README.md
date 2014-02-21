## Charivari
A granular synthesis engine created for a sound-based installation piece which was titled *Charivari (2013)*.

__[K. Michael Fox](http://www.kmichaelfox.com) || [e-mail](mailto:kmichaelfox.contact@gmail.com)__

### Description
The technical goal of this piece, and the code to realize it, was to create an environment which utilized granular synthesis in a non-stream context while maintaining more structure than a stochastic cloud (Roads). Individual grains are stored as metadata-like objects and synthesized one at a time. These grains are then paired together in sequences that make up simply motivated aLife-like organisms (or agents). Thus, the slow change of each organism's sound reflects certain stochastic evolutions and generational reproduction which pass on components of their predecessor's sound. These organisms were originally played through an 8-channel audio system, though there is a simple stereo reduction included in the files. An alternate description and short documentation can be found at the link above.

### Setup
These files are run by Supercollider, which, if you don't already have it, must be downloaded at [http://supercollider.sourceforge.net](http://supercollider.sourceforge.net).

The folder included in this repository, which is titled *SCGrainOrganisms*, contains the two class files required to run the synthesis engine. The folder must be added to your extensions folder for Supercollider. To find out where Supercollider is looking on your system, open Supercollider and use it to evaluate the two following lines:

> _Platform.userExtensionsDir_

> _Platform.systemExtensionsDir_

Do a Google search for a how-to if you want it to look elsewhere. Otherwise, paste the whole *SCGrainOrganisms* folder into one of the extension folders (either user or system). Note: The folder may not exist, in which case it should just be created in the appropriate place, i.e. the address on your HD which one of the above two commands prints. 

The other two files maybe stored any place on the drive.

Evaluate either the stereo (most likely) or the 8-channel (if you for some reason have access to it) synthdefs, but not both, in the file *GrainSynthDefs.scd* to define the necessary synths. After all of that is done, and the server is booted, evaluate the only line of code in *function_demo.scd* to begin the autonomous system.