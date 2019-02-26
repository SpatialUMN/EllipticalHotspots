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
See [application domain](https://github.com/SpatialUMN/EllipticalHotspots/wiki/Application-Domain) to see where you can use elliptical hotspot detection

# Usage  
## Input Data Format  
We need 1 input file [`activity`](https://github.com/SpatialUMN/EllipticalHotspots/blob/master/activity.csv). It has 3 attributes:  
`ID` is the activity id.   
`X` is the x-axis value of the activity.  
`Y ` is the Y-axis value of the activity.  

## Download and Run  
### [How to import a GitHub project into Eclipse](https://github.com/collab-uniba/socialcde4eclipse/wiki/How-to-import-a-GitHub-project-into-Eclipse)  
### Set Variables   
Open [`RunElliptic.java`](https://github.com/SpatialUMN/EllipticalHotspots/blob/master/src/elliptical/RunElliptic.java) file, change line 6 - 9.  
`dataset_path` is the path to your activity file.  
`Method` set Method = 1 if you want to use grid method, set Method = 0 if you need naive method.  
`step_size` only has effect if you choose naive method. It is the step length used on denominator.  
`theta` only has effect if you choose grid method. It is the log likelihood ratio threshold.   
### Output Result
* For Grid-based Approach here is an outcome example you might see.  
> Grid-based Approach.  
Activity Set |A| = 2003  
Study Area S = 999360.0988  
Step length = 2.5  
Number of blocks (400, 400)  
logLR = 730.3276262488107 center = (210, 209), radius = (39, 20) B = 31.86689801904768  
logLR = 730.3276262488107 center = (210, 210), radius = (39, 20) B = 31.86689801904768  
logLR = 731.616170850715 center = (209, 209), radius = (40, 20) B = 32.673654930922304  
logLR = 730.2452730998634 center = (210, 209), radius = (41, 20) B = 33.48041184279693  
maxLR = 731.616170850715maxLR is as big as 731.616170850715  
Grid based approach: 76913 milliseconds  
* For Naive Approach, the result will looks like [this](https://github.com/SpatialUMN/EllipticalHotspots/blob/master/naive_output.txt)
