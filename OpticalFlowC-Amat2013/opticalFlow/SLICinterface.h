/*
*  SLICinterface.h
*
*  Created on: December, 2011
*      Author: Fernando Amat
*

*     Copyright (C) 2011 by  Fernando Amat
*     See licenseOpticalFlowSupervoxels.txt for full license and copyright notice.
*     
*     \brief  wrapper around interface for the SLIC class in order to be able to call it using our UINT16 3D volume images read with MYlib
*/


#if !defined(_SLIC_INTERFACE_H_INCLUDED_)
#define _SLIC_INTERFACE_H_INCLUDED_

#include <string>

namespace mylib {

#include "../mylib/mylib.h"
#include "../mylib/array.h"
#include "../mylib/image.h"
#include "../mylib/water.shed.h"

}

using namespace std;

/*
 * \brief Test or example function on how to use the functions in this file
 */
int mainTestOpticalFlow( int argc, const char* argv[] );

/*
 * \brief Main function to call to calculate optical flow between two time points
 */
int opticalFlow_anisotropy5(const string& filenameInSource,const  string& filenameInTarget,const string& filenameInTargetMask,mylib::uint8 maskThr,mylib::Array*& imgFlow,float maxDistancePartitionNeigh);

//m=10.0 is the value recommended in the paper
//STEP: supervoxel size ~= step*step*step/anisotroptyZ
//imgBckFwdMask contains foreground/background mask in order to only seed super pixels in the foreground
int SLICsupervoxels(mylib::Array *img,int**& klabels,int &numlabels,int STEP,int m=10.0,double anisotropyZ=1.0,mylib::Array *imgBckFwdMask=NULL,mylib::uint8 maskThr=0);

int SLICsuperpixels(mylib::Array *img,int**& klabels,int &numlabels,int STEP,int m=10.0,mylib::Array *imgBckFwdMask=NULL,mylib::uint8 maskThr=0);


/*
 * \brief Parses the results of SLIC superpixels into a labeled array and a partition graph associated with it
 */
mylib::Partition* parseVoxelLabelsToMylibPartition(int **klabels,int& numlabels,mylib::Array *img,mylib::Array *imgLabels);

/*
 * \brief Same as above but it allows a UINT* mask to decide which pixels should not be considered. imgBckFwdMask[0]->voxel is not considered.
 */
mylib::Partition* parseVoxelLabelsToMylibPartition(int **klabels,int& numlabels,mylib::Array *img,mylib::Array *imgLabels,mylib::Array *imgBckFwdMask,mylib::uint8 maskThr,int STEP);



int parseSLICVoxelLabelsToMylibArray(int **klabels,int& numlabels,mylib::Array *imgLabels,mylib::Array *imgBckFwdMask,mylib::uint8 maskThr);
mylib::Partition* generateMylibPartition(int& numlabels,mylib::Array *img,mylib::Array *imgLabels);



#endif // !defined(_SLIC_H_INCLUDED_)
