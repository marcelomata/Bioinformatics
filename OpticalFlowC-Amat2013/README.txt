Copyright (C) 2011 by  Fernando Amat
See licenseOpticalFlowSupervoxels.txt for full license and copyright notice.
Auhtor can be contacted at amatf...@at@...janelia.hhmi.org   (substitute ...@at@... by @)

1.-INTRODUCTION

This code calculates optical flow between two 3D volumes using a Markov Random Field (MRF) over a set of supervoxels in order to improve accuracy and speed up results. It has mainly been testedin large (over 500MB per stack) 3D+time fluorescent microscopy images. However, it should work on any type of image.

==================================================================================

2.-DISCLAIMER AND CITATIONS

This software is intended for research purposes. Any commercial application that intends to use Raptor should contact the authors first. Please, if you use thsi software in your research cite the following paper:

[1] F. Amat, E.W. Myers and P.J. Keller. "Fast and robust optical flow for time-lapse microscopy using super-voxels". Submitted to Bioinformatics.

This software is open-source (see licenseOpticalFlowSupervoxels.txt) and it uses two other open-source libraries:

-SLIC supervoxels, developed by Radhakrishna Achanta at EPFL. Reference: Radhakrishna Achanta, Appu Shaji, Kevin Smith, Aurelien Lucchi, Pascal Fua, and Sabine Süsstrunk. "SLIC Superpixels Compared to State-of-the-art Superpixel Methods". IEEE Transactions on PAMI, May 2012

-Mylib, developed by Dr. Eugene W. Myers at Janelia Farm Research Campus-HHMI.

==================================================================================

3.-INSTALLATION

In the root folder there is CMakeLists.txt file to compile the code using CMake. For instructions on how to do this visit http://www.cmake.org/

The code is self-contained (does not need any external library) and it has been tested in Unix platforms as well as Microsoft Windows.

Note: if you are using Eclipse, you have to create the Eclipse project in a different folder than the source files. Otehrwise Eclipse will not compile the project properly.

====================================================================================

4.-USAGE

Example:

1.-Uncompress the file syntheticData.rar. It contains two synthetic images to test the program easily.

2.-Call the program as follows:


terminal> opticalFlow syntheticData/rawData/TM00000  syntheticData/rawData/TM00001 syntheticData/rawData/test_backgroundPredictionIlastik_00001 syntheticData/opticalFlowOutput.bin 25


The optical flow from TM00001 to TM00000 should be stored in binary file syntheticData/opticalFlowOutput.bin. The output binary file is stored in floats and has three channels (x,y,z optical float), with each channel beign the same size as the input images.

The file syntheticData/rawData/test_backgroundPredictionIlastik_00001 is a uint8 tiff file of the same size as the input images representing the foreground/background mask of syntheticData/rawData/TM00001. As explained in [1], fluorescent microscopy images tend to be sparse, so we only need to calculate the optical flow in a small subset of foreground pixels. Any value>0 in the uint8 stack will be considered foreground.

The last parameter is d_max (see [1] for more details). It is the distance threshold between the center of two supervoxels to connect them with an edge in the MRF. This parameter has to be tunned depending on your application.


You can check the function int mainTestOpticalFlow( int argc, const char* argv[] ) in the file SLICinterface.cpp to see how of these parameters are used.


4.1-ADVANCED USAGE:

There are a few other parameters that are hard coded inside the code because they should not be changed from application to application. However, they are all defined (and explained) at the beginning of the function int opticalFlow_anisotropy5(...) in the file SLICinterface.cpp. You can change their values and recompile the code if you want to see the effect of each of them.


===========================================================================================




