# APHMAL
This was created with help from Gunter Mussbacher as a semester long project for ECSE 539


The following is a description of the files/file structure.
**For all other info see ECSE539ProjectReport.pdf** (Best to start by reading that)

## Files
`metamodel/` contains metamodel related files (ecore included for convenience, not necessary)

`model/` contains input & output model files. `*.flexmi` files are input. `*.model` files are output. `example*.flexmi` files are "Sample input files". `name*.flexmi` are additional input files, used for testing, included for reference. Output files should be deleted and re-created to test transformation but are included for reference.

For example:
`model/example1.flexmi` creates `model/output1.model` and is run by `transform/transform.launch`


`transform/` contains the transformation source file. (untested) exported .launch configurations have been included for convenience.

`bin/` contains the `.jar` file for the ETL Java native extension tool. ** IT IS ESSENTIAL TO RUN THE TRANSFORMATION **. Place this in your Eclipse Application's `Contents/Eclipse/dropins` folder (as described in ProjectReport.pdf) and ** RESTART ECLIPSE **

`org.eclipse.epsilon.examples.tools` contains the source for the .jar, as well as various plugin and extension point settings

`video/` contains txt file with link to video. (https://www.screencast.com/t/jGLOoUwIlo5)
