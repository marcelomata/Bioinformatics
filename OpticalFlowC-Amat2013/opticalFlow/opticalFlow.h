/*
*  opticalFlow.h
*
*  Created on: December 2011
*      Author: Fernando Amat
*

*     Copyright (C) 2011 by  Fernando Amat
*     See licenseOpticalFlowSupervoxels.txt for full license and copyright notice.
*     
*     \brief Containes main functions to calculate optical flow using MRF on supervoxels
*/

#ifndef OPTICALFLOW_H_
#define OPTICALFLOW_H_

#include <vector>
#include <iostream>
#include <math.h>

#include "../external/lbfgs/lbfgs.h"
#include "multithreadLBFGS.h"
//#include "../external/lbfgsb3.0/LBFGSBwrapper.h"

namespace mylib {
	#include "../mylib/mylib.h"
	#include "../mylib/array.h"
	#include "../mylib/water.shed.h"
	#include "../mylib/region.h"
}

using namespace std;

//auxiliary structure to store 3D flow
struct flow3D
{
	float x,y,z;

	flow3D(void){x=y=z=0.0f;};

	void reset(void){x=y=z=0.0f;};
};

//----------------------routines and sructures needed for the floodfill
struct FloodFillArgs//struct needed to pass arguments to flood fill operation
{
	mylib::uint32* imgLabelsPtr;
	unsigned char* map;
	mylib::uint32 label;//current label to generate flood fill
	mylib::uint32 countL;//final value of the label
};

#define Marg(a) ((FloodFillArgs*) (a))

//first pass over flood fill region
inline mylib::boolean testFF(mylib::Indx_Type p, void *arg)
{
	return (Marg(arg)->imgLabelsPtr[p] == Marg(arg)->label);
};

//second pass over flodd fill region
inline void actFF(mylib::Indx_Type p, void *arg)
{
	Marg(arg)->map[p] = 1;
	Marg(arg)->imgLabelsPtr[p] = Marg(arg)->countL;
};
//----------------------------------------------------

//----------------------------------------------------

/*
 *
 * \brief Main routine to compute optical flow in 3D using a partition (such as supervoxels) of an image
 *
 * \warning flow should be initialized so we can call the code in cascade for different pyramid levels
 *
 * TODO: how to handle different pyramid levels
 */
int calculateOpticalFlow(mylib::Array *imSource,mylib::Array *imTarget,mylib::Partition *imTargetPartition,vector<flow3D> &flow,float* scale,globs_LBFGS_& lbfgs_param);


/*
 *
 * \brief Function to compute residual value and its derivative in order to minimize the problem
 */
void funcgrad_residOpticalFlow (double* x, double& f, double* g, void *params);
inline void funcgrad_residOpticalFlowAP (ap::real_1d_array x, double& f, ap::real_1d_array& g, void *params){funcgrad_residOpticalFlow(x.getcontent(), f, g.getcontent(), params);};

/*
 * \brief Used for debugging purposes. It prints infor for each iteration
 */
void newiteration_residOpticalFlow(int iter, const double* x, double f,const double* g, void *params);
inline void newiteration_residOpticalFlowAP(int iter, const ap::real_1d_array& x, double f,const ap::real_1d_array& g, void *params){newiteration_residOpticalFlow(iter, x.getcontent(),f,g.getcontent(), params);};
/*
 * \brief calculates derivatives along different axis
 *
 *\param[in] scale It should have the same length as im->ndimns and indicates anisotropy in the image resolution along different axis
 */
void calculateArrayGradient(mylib::Array *im,mylib::Array**& imGrad,const float* scale);

/*
 * \brief function to calculate data term (and its gradient) for each region (i.e. super-pixel)
 */

void calculateDataTermOneRegion(const mylib::Region* regionP,const mylib::Array* imTarget,const mylib::Array** imTargetDer,const mylib::uint16* imSourcePtr,const double* x,const float *scale,double deltaHdataTerm,double &fAux,double* gAux);


/*
 * \brief Build pyramid for image label. This function uses the very special case of anisotropyZ=5, so the first 2 levels only x,y are downsampled.
 *
 */
int buildPyramidImageLabel_anisotropy5(mylib::Array *imgLabel, mylib::Array**& imgLabelPyramid,int numLabels,int*& numLabelsPyramid,int numLevels);


/*
 * \brief interpolates img at point x using N one-dimensional cubic polynomials
 * \brief if df is not null it also computes the one-directional derivatives
 */

int nCubicInterpolation(const double *x,const float* scale,mylib::Array *img,mylib::Array** img_der,double &f,double *df);

//we need coordinate c to avoid make/free for each pixel in the image
int trilinearInterpolation(const double *x,const float* scale,const mylib::Array *img,const mylib::Array** img_der,double &f,double *df,mylib::Coordinate* c);

/*
 * \brief Generates an RGB image with the flow at each voxel. It is a way to go from regions to voxels
 */
mylib::Array* getFlowArrayFromRegions(mylib::Partition* imgPartition,const vector<flow3D>& flow);
int getFlowRegionsFromFlowArray_anisotropy5(mylib::Array*flowArray,mylib::Partition* imgLabelPartition,int pyramidLevel,vector<flow3D>& flow,const vector<vector<pair<int,double> > >& partitionNeighbors);

inline mylib::uint32 mode(mylib::Frame* frame,mylib::uint32* countLabel,int maxNumberUniqueLabels);

/*
 * \brief Brute nearest neighbors search for all the regions closer than maxDistance. We use the centroid of each region as an approximation to calculate the graph
 * \brief Edge (i,j) is only added if i<j (to not duplicate)
 *
 */
void partitionNeighbors(mylib::Partition* imgLabelPartition, float maxDistance,const float* scale, vector<vector<pair<int,double> > >& partitionNeigh,float &maxVolume);

/*
 * \brief relabels all components so each label is a one-to-one correspondence with a 3^n-1 connected region
 */
int enforceLabelConnectivity(int& numlabels,mylib::Array *imgLabels);


//------------------------------debug functions---------------------------
void writeFlowFromPartition(ostream &out,mylib::Partition* imgLabelPartition,const vector<flow3D>& flow);

int calculateEnergyOpticalFlow(mylib::Array *flowArray,globs_LBFGS_ *glob_param);


#endif /* OPTICALFLOW_H_ */
