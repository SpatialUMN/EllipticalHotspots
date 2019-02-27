# Elliptical Hotspot Detection
* [What Can You Get From Elliptical Hotspot Detection](https://github.com/SpatialUMN/EllipticalHotspots/blob/master/README.md#what-can-you-get)  
* [Usage](https://github.com/SpatialUMN/EllipticalHotspots/blob/master/README.md#usage)   
  * [Input Data Format](https://github.com/SpatialUMN/EllipticalHotspots/blob/master/README.md#input-data-format)  
  * [Download and Run](https://github.com/SpatialUMN/EllipticalHotspots/blob/master/README.md#Download-and-Run)  
    * [How to import a GitHub project into Eclipse](https://github.com/collab-uniba/socialcde4eclipse/wiki/How-to-import-a-GitHub-project-into-Eclipse)  
    * [Set Variables](https://github.com/SpatialUMN/EllipticalHotspots/blob/master/README.md#set-variables) 
    * [Output Results](https://github.com/SpatialUMN/EllipticalHotspots/blob/master/README.md#output-results)
* [Code Explanation (Java Diagram)]() 
* [Case Study]()  
* [Bug Report](https://github.com/SpatialUMN/EllipticalHotspots/issues)  
  

# What Can You Get
Elliptical Hotspot Detection (EHD) finds ellipse shaped hotspot areas where the concentration of activities inside is significantly higher
than the concentration of activities outside.   
![E1b](https://github.com/SpatialUMN/EllipticalHotspots/blob/master/images/E1b.PNG)  
See [application domain](https://github.com/SpatialUMN/EllipticalHotspots/wiki/Application-Domain) to see where you can use elliptical hotspot detection.  
Basic [concepts](https://github.com/SpatialUMN/EllipticalHotspots/wiki/Basic-Concepts) can help better understanding the problem.  
For academic users, see [here]() for algorithm explanation.

# Usage  
## Input Data Format  
We need 1 input file [`activity`](https://github.com/SpatialUMN/EllipticalHotspots/blob/master/activity.csv). It has 3 attributes:  
`ID` is the activity id.   
`X` is the x-axis value of the activity.  
`Y ` is the Y-axis value of the activity.  

## Download and Run  
### [How to import a GitHub project into Eclipse](https://github.com/collab-uniba/socialcde4eclipse/wiki/How-to-import-a-GitHub-project-into-Eclipse)  
### Set Variables   
Open [`RunElliptic.java`](https://github.com/SpatialUMN/EllipticalHotspots/blob/master/src/elliptical/RunElliptic.java) file, change line 6 and 7.  
`dataset_path` is the path to your activity file.   
`theta` only has effect if you choose grid method. It is the log likelihood ratio threshold.   
### Output Results
The output will contain the method name, basic information of the study area, all the ellipse information, and the running time.  
[Here](https://github.com/SpatialUMN/EllipticalHotspots/blob/master/Grid_output) is an outcome example you might see.   
