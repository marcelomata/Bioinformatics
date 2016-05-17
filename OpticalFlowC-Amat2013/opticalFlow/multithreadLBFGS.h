/*
*  multithreadLBFGS.h
*
*  Created on: February, 2012
*      Author: Fernando Amat
*

*     Copyright (C) 2011 by  Fernando Amat
*     See licenseOpticalFlowSupervoxels.txt for full license and copyright notice.
*     
*     \brief Class to parallelize calculating the energy terms during optimization
*	  \warning Unfinished class
*/

#ifndef MULTITHREADLBFGS_H_
#define MULTITHREADLBFGS_H_

#include <iostream>
#include<vector>
#include <queue>

#include "../external/lbfgs/lbfgs.h"

#define INTERP_TRILINEAR //decides the kind of interpolation we want

#define  R(x) x //redefine in order to be able to compile mylib

namespace mylib{
	#include "../mylib/region.h"
	#include "../mylib/array.h"
	#include "../mylib/connectivity.h"
};

class multithreadLBFGS;//forward declaration

using namespace std;

//-------------------------------------------------------------------------
//pointer to additional data needed for L-BFGS optimization routine
struct globs_LBFGS_
{
	mylib::Array *imSource;
	mylib::Array *imTarget;
	mylib::Partition *imTargetPartition;
	mylib::Array *imTarget_dx;
	mylib::Array *imTarget_dy;
	mylib::Array *imTarget_dz;
	float* scale;//to account for anisotropy
	vector< vector< pair<int,double> > >* partitionNeigh;//contains the lists of neighbors for each partition (with no duplicates)
	mylib::Region** regionPvec;
	int pyramidLevel;

	//optimization parameters:THEY SHOULD BE SET BEFORE CALLING THE FUNCTION
	double lambda;//smooth term weight
	double deltaHsmoothTerm;//Huber penalty constant for pairiwse smoothing term
	double deltaHdataTerm;//Huber penalty constant for data (singleton) term
	float maxVolume;//largest volume in the set of regions to normalize factors

	multithreadLBFGS *producer;	

	//debugging variables
	int verbose;//0->no debugging;>0 indicates different levels of debugging info
	double fData,fSmooth;
};

//---------------------------------------------------------------------------
//functions for minimization
void funcgrad_residOpticalFlow_mt (ap::real_1d_array x, double& f, ap::real_1d_array& g, void *params);
void newiteration_residOpticalFlow_mt(int iter, const ap::real_1d_array& x, double f,const ap::real_1d_array& g, void *params);

/*
 * \brief Huber(r,delta) loss function (cost and its derivative to be more efficient)
 */
inline void HuberCostAndDer(double r,double delta,double& f,double& der)
{
	const int costFunction=0;//0->Huber;1->TV;2->German&McClure
	
	switch(costFunction)
	{
	case 0:
	//------------Huber---------------------
	if(delta<=0.0)//L1 norm
	{
		f=fabs(r);
		if(r==0) der=0;
		else if(r<0) der=-1.0;
		else der=1.0;
	}else if(fabs(r)<delta)//L2 regime
	{
		f=0.5*r*r;
		der=r;
	}else{//L1 regime
		f=delta*(fabs(r)-0.5*delta);
		if(r<0) der=-delta;
		else der=delta;
	}
	break;
	
	//-----------------TV-----------------------
	case 1:
	//delta=0.01;
	f=sqrt(r*r+delta*delta);
	der=r/f;
	break;

	case 2:
	//----------------Geman & McClure
	//delta=2.0;
	double aux=r*r;
	f=aux/(delta+aux);
	aux+=delta;
	der=2.0*r*delta/(aux*aux);
	break;
	}
}

//-----------------------------------------------------
//from Mylib to allow const elements
#define ADIMN2(a)     ((mylib::Dimn_Type *) (a)->data)

inline mylib::Coordinate* coord4idx(int n,const mylib::Dimn_Type *d, mylib::Indx_Type idx, mylib::string routine)
{ mylib::Dimn_Type   dims[1], m, *l;
  mylib::Coordinate *lat;
  int         i;

  dims[0] = n;
  lat     = mylib::Make_Array(mylib::PLAIN_KIND,mylib::DIMN_TYPE,1,dims);

  l = ADIMN2(lat);
  for (i = 0; i < n; i++)
    { m = d[i];
      l[i] = (mylib::Dimn_Type) (idx % m);
      idx /= m;
    }
  if (idx > 0)
    { fprintf(stderr,"Index is out of array boundary (%s)\n",routine);
      exit (1);
    }
  return (lat);
}

//----------------------------------------------------------
//main class to generate multithread code

class multithreadLBFGS
{
	public:
	multithreadLBFGS(mylib::Region** regionPvec,const mylib::Array** imTargetDer,ap::real_1d_array& x,const float *scale,double &f,int numPini,int numPend,globs_LBFGS_ lbfgs_param,int workerPid):
	  regionPvec(regionPvec),imTargetDer(imTargetDer),x(x),scale(scale),f(f),numPini(numPini),numPend(numPend),lbfgs_param(lbfgs_param),workerPid(workerPid){};

	void operator()()//called when thread starts
	{
		//TODO: setup producer and consumer	
	}
	void minimize();
	//calculates all the assigned data terms
	void calculate(double* gAux);


	//consumer producer code 
	void pushJob()
    {
        //TODO
    }

    bool jobDone() const//true->workers are done
    {
		//TODO
		return false;
    }


    void wait_and_pop()
    {
        //TODO
    }
	
private:

	globs_LBFGS_ lbfgs_param;

	//variables needed to solve data term
	mylib::Region** regionPvec;
	const mylib::Array** imTargetDer;
	ap::real_1d_array& x;
	const float *scale;
	double &f;
	int numPini;
	int numPend;
	
	//variables needed for thread synchronization: model from http://www.justsoftwaresolutions.co.uk/threading/implementing-a-thread-safe-queue-using-condition-variables.html
	static const int numWorkers=10;
	unsigned int workerBusy[numWorkers];//euqal to zero if i-th worker is done;non-zero otherwise
	int workerPid;
    
	//------------------------------functions to compute minimization----------------------------------
	//calculates term for a single region
	void calculateDataTermOneRegion(const mylib::Region* regionP,const mylib::Array* imTarget,const mylib::Array** imTargetDer,const mylib::uint16* imSourcePtr,const double* x,const float *scale,double deltaHdataTerm,double &fAux,double* gAux);
	int trilinearInterpolation(const double *x,const float* scale,const mylib::Array *img,const mylib::Array** img_der,double &f,double *df,mylib::Coordinate* c);

};

#endif /*MULTITHREADLBFGS_H_*/