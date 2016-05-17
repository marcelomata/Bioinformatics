/*
 * opticalFlow.cpp
 *
 *  Created on: Dec 28, 2011
 *      Author: Fernando Amat
 */

//threads
//#include <boost/thread/thread.hpp>//if you include after all others includes it gives weird errors

#include <map>
#include <stdio.h>
#include <stdlib.h>
#include <fstream>
#include "opticalFlow.h"
#include <omp.h>

namespace mylib{
#include "../mylib/filters.h"
#include "../mylib/region.h"
#include "../mylib/image.h"
#include "../mylib/connectivity.h"
}

#define INTERP_TRILINEAR //decides the kind of interpolation we want

/*
class fDataTermThread{

public:
	fDataTermThread(mylib::Region** regionPvec,const mylib::Array* imTarget,const mylib::Array** imTargetDer,const mylib::uint16* imSourcePtr,double* x,const float *scale,double deltaHdataTerm,double &f,double* gAux,int numPini,int numPend):
	  regionPvec(regionPvec),imTarget(imTarget),imTargetDer(imTargetDer),imSourcePtr(imSourcePtr),x(x),scale(scale),deltaHdataTerm(deltaHdataTerm),f(f),gAux(gAux),numPini(numPini),numPend(numPend){};

	void operator()()//called when thread starts
	{
		f=0.0;
		double fAux;
		int ndims=imTarget->ndims;
		int numPpos=numPini*ndims;

		for(int numP=numPini;numP<numPend;numP++,numPpos+=ndims)
		{
			calculateDataTermOneRegion(regionPvec[numP],imTarget,imTargetDer,imSourcePtr,x+numPpos,scale,deltaHdataTerm,fAux,gAux+numPpos);
			f+=fAux;
		}
	}

private:
	mylib::Region** regionPvec;
	const mylib::Array* imTarget;
	const mylib::Array** imTargetDer;
	const mylib::uint16* imSourcePtr;
	double* x;
	const float *scale;
	double deltaHdataTerm;
	double &f;
	double* gAux;
	int numPini;
	int numPend;
};
*/


int calculateOpticalFlow(mylib::Array *imSource,mylib::Array *imTarget,mylib::Partition *imTargetPartition,vector<flow3D> &flow, float* scale,globs_LBFGS_ &lbfgs_param)
{
	if((imSource->ndims != 3 || imTarget->ndims !=3 ) && (imSource->ndims != 2 || imTarget->ndims !=2))
	{
		cout<<"ERROR: calculateOpticalFlow: code is only ready for 2D and 3D volumes"<<endl;
		return 2;
	}

	int ndims=imSource->ndims;
	if(imSource->dims[0] != imTarget->dims[0] || imSource->dims[1] != imTarget->dims[1])
	{
		cout<<"ERROR: calculateOpticalFlow: image target and image source are not the same size"<<endl;
		return 1;
	}
	if(ndims>2)
	{
		if(imSource->dims[2] != imTarget->dims[2])
	{
		cout<<"ERROR: calculateOpticalFlow: image target and image source are not the same size"<<endl;
		return 1;
	}
	}



	//initialize flow to zero in case it is empty
	unsigned int Np=mylib::Get_Partition_Vertex_Count(imTargetPartition);
	if(flow.empty()) flow.resize(Np);

	if(flow.size()!=Np)
	{
		cout<<"ERROR: calculateOpticalFlow: vector<flow> size does not match with the number of partitions"<<endl;
		return 3;
	}


	//calculate data necessary to evaluate residual
	lbfgs_param.imSource=imSource;
	lbfgs_param.imTarget=imTarget;
	lbfgs_param.imTargetPartition=imTargetPartition;
	lbfgs_param.scale=scale;

	//calculate derivatives in the grid;
	mylib::Array** imGrad=NULL;
	calculateArrayGradient(imTarget,imGrad,scale);
	lbfgs_param.imTarget_dx=imGrad[0];
	lbfgs_param.imTarget_dy=imGrad[1];
	if(ndims>2)
		lbfgs_param.imTarget_dz=imGrad[2];
	else
		lbfgs_param.imTarget_dz=NULL;

	//precompute regions for each superpixel. Otherwise we have to do flood fill in each gradient iteration
	int numPartitions=mylib::Get_Partition_Vertex_Count(imTargetPartition);
	mylib::Region **regionPvec=(mylib::Region**)(malloc(sizeof(mylib::Region*)*numPartitions));
	for(int numP=0;numP<numPartitions;numP++)
	{
		regionPvec[numP]=mylib::Record_P_Vertex(imTargetPartition,numP,1,0);//regions can have holes
	}
	lbfgs_param.regionPvec=regionPvec;
	//====================================================================================================



	//call minimization routine
	double epsg=1e-6;//relative change for gradient
	double epsf=1e-6;//relative change for cost function
	double epsx=1e-6;//relative change for x
	//double absBounds[3]={12,12,1};//bounds at the zero level of the pyramid
	//int useBounds=0;//2->bounds are considered;0->bounds are not considered
	int maxits=150;
	int info=0;
	int N=ndims*Np;//we need to concatenate flow as x=[u_1,v_1,w_1,....,u_N,v_N,w_N] (cache friendly)
	int M=6;
	ap::real_1d_array x;
	x.setbounds(1,N);
	int count=0;
	for(unsigned int ii=0;ii<Np;ii++,count+=ndims)
	{
		x(count+1)=flow[ii].x;
		x(count+2)=flow[ii].y;
		if(ndims>2) x(count+3)=flow[ii].z;
	}

	//------------------------------------debug: test funcgrad_residOpticalFlow-------------------------
	/*
	cout<<"DEBUG: calculateOpticalFlow: funcgrad_residOpticalFlow function"<<endl;
	ap::real_1d_array gDebug;
	gDebug.setbounds(1,N);
	double fDebug;
	count=0;
	for(unsigned int ii=0;ii<Np;ii++,count+=3)
	{
		x(count+1)=(drand48()-0.5)*1;x(count+2)=(drand48()-0.5)*1;x(count+3)=(drand48()-0.5)*1;
		//x(count+1)=0;x(count+2)=0;x(count+3)=0;
	}
	funcgrad_residOpticalFlow(x,fDebug,gDebug,&lbfgs_param);
	exit(3);
	 */
	//--------------------------------------------------------------------------------------------------

	if(1)//use lbfgs with no boundary constraints
	{
		cout<<"Using L-BFGS unconstrained optimization"<<endl;
		if(lbfgs_param.verbose==0)
			lbfgsminimize(N,M,x,funcgrad_residOpticalFlowAP,NULL,&lbfgs_param,epsg,epsf,epsx,maxits,info);
		else
			lbfgsminimize(N,M,x,funcgrad_residOpticalFlowAP,newiteration_residOpticalFlowAP,&lbfgs_param,epsg,epsf,epsx,maxits,info);
	}
	/*
	 //In this particular it does not seem to be an advantage and the code required f2c library, so we commented it out to avoid the dependency
	else{//use lbfgs with boundary constraints
		//NOTE: bounded LBFGS does not seem to help but code is about 50% faster

		if(useBounds>0)
			cout<<"Using L-BFGS-Bounded optimization with abs bounds "<<absBounds[0]/pow(2.0,lbfgs_param.pyramidLevel)<<" "<<absBounds[1]/pow(2.0,lbfgs_param.pyramidLevel)<<" "<<absBounds[2]<<endl;
		else
			cout<<"Using L-BFGS-Bounded optimization with no bounds"<<endl;

		double factr=1e7;
		integer Ni=N;
		integer Mi=M;
		double *l=new double[N];
		double *u=new double[N];
		integer *nbd=new integer[N];

		for(int ii=0;ii<N;ii+=ndims)
		{
			for(int jj=0;jj<ndims;jj++) 
			{
				nbd[ii+jj]=useBounds;//2->bounds are considered;0->bounds are not considered
				u[ii+jj]=absBounds[jj];
				l[ii+jj]=-absBounds[jj];
				if(jj<2)
				{
					u[ii+jj]/=pow(2.0,lbfgs_param.pyramidLevel);
					l[ii+jj]/=pow(2.0,lbfgs_param.pyramidLevel);
				}
			}
		}

		if(lbfgs_param.verbose==0)
			lbfgsbminimize(Ni, Mi, x.getcontent(), l, u, nbd, funcgrad_residOpticalFlow, NULL, &lbfgs_param, factr, epsg, maxits, info);
		else
			lbfgsbminimize(Ni, Mi, x.getcontent(), l, u, nbd, funcgrad_residOpticalFlow, newiteration_residOpticalFlow, &lbfgs_param, factr, epsg, maxits, info);

		//release memory
		delete []l;delete []u;delete []nbd;

	}
	 */
	
	//tricks from classic++ algorithm
	//TODO: implement median filter in the flow after each iteration in th eoptimization procedure
	//TODO: try to limit maximum flow displacement at each iteration

	//copy final solution back
	count=0;
	for(unsigned int ii=0;ii<Np;ii++,count+=ndims)
	{
		flow[ii].x=(float)(x(count+1));
		flow[ii].y=(float)(x(count+2));
		if(ndims>2) flow[ii].z=(float)(x(count+3));
	}

	//release memory
	mylib::Free_Array(lbfgs_param.imTarget_dx);
	lbfgs_param.imTarget_dx=NULL;
	mylib::Free_Array(lbfgs_param.imTarget_dy);
	lbfgs_param.imTarget_dy=NULL;
	if(ndims>2) mylib::Free_Array(lbfgs_param.imTarget_dz);
	lbfgs_param.imTarget_dz=NULL;
	for(int numP=0;numP<numPartitions;numP++) mylib::Free_Region(regionPvec[numP]);
	std::free(regionPvec);
	lbfgs_param.regionPvec=NULL;


	if(imGrad!=NULL)
	{
		std::free(imGrad);
	}

	return 0;
}


//==================================================================================================
/*
 *
 * The cost function considered here is the following (with u(x,y,z),v(x,y,z),w(x,y,z) the flow at each pixel or super-pixel)
 *
 * E(u,v,w)=\sum_{s\inS}\sum_{n\in s} Huber(r(u_s,v_s,w_s),deltaHdataTerm) + \lambda \sum_{s \in S}\sum_{a\in Neigh(s) s.t. a<s} \left[ Huber(u_s-u_a,deltaHsmoothTerm) + Huber(v_s-v_a,deltaHsmoothTerm) + Huber(w_s-w_a,deltaHsmoothTerm) \right]
 *
 * S is the set of superpixels (stores in the partition). Thus, we impose that the flow is the same for voxels belonging to teh same supervoxel
 *
 * r(u,v,w)=I(x+u,y+v,z+w,t+1)-I(x,y,z,t)=imTarget(p+uvw)-imSource(p)
 *
 * I(x+u,y+v,z+w,t+1) and its partial derivatives need to be calculated using interpolation. As usual it is a trade-off between accuracy and speed.
 */
void funcgrad_residOpticalFlow (double* x, double& f, double* g, void *params)
{
	globs_LBFGS_ *glob_param=(globs_LBFGS_*)params;
	mylib::Partition* imTargetPartition=glob_param->imTargetPartition;
	mylib::Array* imSource=glob_param->imSource;
	mylib::Array* imTarget=glob_param->imTarget;
	mylib::Array* imTarget_dx=glob_param->imTarget_dx;
	mylib::Array* imTarget_dy=glob_param->imTarget_dy;
	mylib::Array* imTarget_dz=glob_param->imTarget_dz;
	vector<vector<pair<int,double> > >* partitionNeigh=glob_param->partitionNeigh;
	mylib::Region** regionPvec=glob_param->regionPvec;

	double lambda=glob_param->lambda;
	double deltaHsmoothTerm=glob_param->deltaHsmoothTerm;
	double deltaHdataTerm=glob_param->deltaHdataTerm;
	float* scale=glob_param->scale;

	if(imSource->type!=mylib::UINT16_TYPE || imTarget->type!=mylib::UINT16_TYPE)
	{
		cout<<"ERROR: funcgrad_residOpticalFlow: code expects UINT16 images"<<endl;
		exit(2);
	}

	mylib::uint16* imSourcePtr=(mylib::uint16*)(imSource->data);

	//initialize residual and gradient
	int numPartitions=mylib::Get_Partition_Vertex_Count(imTargetPartition);
	f=0;
	double fSmooth=0;
	int ndims=imSource->ndims;
	int sizeX=ndims*numPartitions;
	memset(g,0,sizeof(double)*sizeX);


	int numPpos=0,numNeighPos=0;
	//int *listedges=NULL;
	//int nedges=0;;
	double fAux,gAux,auxU;
	//double auxV,auxW;

	//--------------------------
	/*
	 //needed if we calculate data term directly in here instead of calling the function calculateDataTermOneRegion
	mylib::Size_Type k;
	mylib::Indx_Type p;
	double auxI,der;
	mylib::Coordinate *c=mylib::Make_Array(mylib::PLAIN_KIND,mylib::DIMN_TYPE,1,&ndims);
	//memset(gAux,0,sizeof(double)*(ndims));
	mylib::Region* regionP=NULL;
	double* xx=new double[imTarget->ndims];
	*/
	//-------------------------

#ifdef INTERP_TRILINEAR
	const mylib::Array** imTargetDer=(const mylib::Array**)malloc(sizeof(mylib::Array*)*imTarget->ndims);

	imTargetDer[0]=imTarget_dx;imTargetDer[1]=imTarget_dy;
	if(ndims>2) imTargetDer[2]=imTarget_dz;
#endif
	mylib::Use_Array_Basis(imTarget);//set basis to get all the coordinate indexes

//-------------------------------------------
	/*
	const int numThreads=12;
	double* fVec=new double[numThreads];
	boost::thread_group threads;
	int numPini=0,numPend=0;
	int step=(int)floor(double(numPartitions)/double(numThreads));
    for (int i = 0; i < numThreads-1; ++i)
	{
		numPend+=step;
        threads.create_thread(fDataTermThread(regionPvec,imTarget,imTargetDer,imSourcePtr,x.getcontent(),scale,deltaHdataTerm,fVec[i],g.getcontent(),numPini,numPend));
		numPini+=step;
	}
	threads.create_thread(fDataTermThread(regionPvec,imTarget,imTargetDer,imSourcePtr,x.getcontent(),scale,deltaHdataTerm,fVec[numThreads-1],g.getcontent(),numPini,numPartitions));//residual

    threads.join_all();
	for (int i = 0; i < numThreads; ++i) f+=fVec[i];
	delete[] fVec;
	*/
	//---------------------------------------------------------------------

	
	//single thread execution
	//double beta=100000;//in case we want to regularize (i.e bias) flow to zero
	for(int numP=0;numP<numPartitions;numP++)
	{
		numPpos=numP*ndims;//for parallelization
		calculateDataTermOneRegion(regionPvec[numP],imTarget,imTargetDer,imSourcePtr,x+numPpos,scale,deltaHdataTerm,fAux,g+numPpos);
		f+=fAux;

		
		//regularization
		/*
		for(int aa=0;aa<ndims;aa++)
		{
			f+=0.5*beta*x(numPpos+aa+1)*x(numPpos+1+aa)*scale[aa]*scale[aa];
			g(numPpos+1+aa)+=beta*x(numPpos+1+aa)*scale[aa]*scale[aa];
		}
		*/
		//for(int aa=0;aa<ndims;aa++) g(numPpos+1+aa)+=df[aa];//by passing g directly we avoid double copy
	}
	

	//not needed anymore since I precomputed before minimization
	//mylib::Free_Region(regionP);

	//-------------------------------------------------------------------------
	/*
		//calculate smooth term (value and gradient) using only 2n-connectivity regions (very limited)
		listedges=mylib::Get_Partition_Neighbors (imTargetPartition, numP, &nedges);
		mylib::P_Edge* pEdge=NULL;
		for(int ii=0;ii<nedges;ii++)
		{
			//pEdge->region1 < pEdge->region2 ALWAYS
			pEdge=mylib::Get_Partition_Edge (imTargetPartition, listedges[ii]);
			if(numP==(pEdge->region1))//to avoid double counting we only include edges with numP<neigh
			{
				numNeighPos=3*(pEdge->region2);
				auxU=x(numPpos+1)-x(numNeighPos+1);
				auxV=x(numPpos+2)-x(numNeighPos+2);
				auxW=x(numPpos+3)-x(numNeighPos+3);
				HuberCostAndDer(auxU,deltaHsmoothTerm,fAux,gAux);
				fSmooth+=fAux;
				g(numPpos+1)+=lambda*gAux;// *1.0
				g(numNeighPos+1)-=lambda*gAux;

				HuberCostAndDer(auxV,deltaHsmoothTerm,fAux,gAux);
				fSmooth+=fAux;
				g(numPpos+2)+=lambda*gAux;// *1.0
				g(numNeighPos+2)-=lambda*gAux;

				HuberCostAndDer(auxW,deltaHsmoothTerm,fAux,gAux);
				fSmooth+=fAux;
				g(numPpos+3)+=lambda*gAux;// *1.0
				g(numNeighPos+3)-=lambda*gAux;
			}else{
				break;//pEdge->region1 < pEdge->region2 ALWAYS
			}
		}
	 */
	//--------------------------------------------------------------------------------------------

	//calculate smooth term (value and gradient) using regions closer than maxDistance (precalculated ahead of time)
	numPpos=0;
	for(int numP=0;numP<numPartitions;numP++,numPpos+=ndims)
	{
		vector<pair<int,double> >* edges=&((*partitionNeigh)[numP]);
		double lambdaAux;
		for(vector<pair<int,double> >::const_iterator iter=edges->begin();iter!=edges->end();++iter)//we do not need to avoid double counting because it was already done during neighbor list creation
		{
			numNeighPos=ndims*(iter->first);
			lambdaAux=lambda*(iter->second);


			for(int aa=0;aa<ndims;aa++)
			{
				auxU=(x[numPpos+aa]-x[numNeighPos+aa])*scale[aa];
				HuberCostAndDer(auxU,deltaHsmoothTerm,fAux,gAux);
				fSmooth+=fAux*(iter->second);
				g[numPpos+aa]+=lambdaAux*gAux*scale[aa];//*1.0
				g[numNeighPos+aa]-=lambdaAux*gAux*scale[aa];
			}

			/*
			auxU=(x[numPpos]-x[numNeighPos])*scale[0];
			auxV=(x[numPpos+1]-x[numNeighPos+1])*scale[1];
			auxW=(x[numPpos+2]-x[numNeighPos+2])*scale[2];
			HuberCostAndDer(auxU,deltaHsmoothTerm,fAux,gAux);
			fSmooth+=fAux*(iter->second);
			g[numPpos]+=lambdaAux*gAux*scale[0];// *1.0
			g[numNeighPos]-=lambdaAux*gAux*scale[0];

			HuberCostAndDer(auxV,deltaHsmoothTerm,fAux,gAux);
			fSmooth+=fAux*(iter->second);
			g(numPpos+2)+=lambdaAux*gAux*scale[1];// *1.0
			g(numNeighPos+2)-=lambdaAux*gAux*scale[1];

			HuberCostAndDer(auxW,deltaHsmoothTerm,fAux,gAux);
			fSmooth+=fAux*(iter->second);
			g(numPpos+3)+=lambdaAux*gAux*scale[2];// *1.0
			g(numNeighPos+3)-=lambdaAux*gAux*scale[2];
			*/
		}
	}

	//--------------------------------debug------------------------
	/*
	cout<<"DEBUGGING!!! funcgrad_residOpticalFlow"<<endl;
	//cout.precision(20);
	//cout.setf(ios::fixed,ios::floatfield);
	cout<<"fData="<<f<<";fSmooth="<<fSmooth<<";fData+lambda*fSmooth="<<f+lambda*fSmooth<<endl;
	

	int ini=3*10,end=3*20;
	cout<<"delta_u=[";
	for(int ii=ini;ii<end;ii+=3)
	{
		cout<<x[ii]<<";";
	}
	cout<<"];"<<endl;
	cout<<"delta_v=[";
	for(int ii=ini;ii<end;ii+=3)
	{
		cout<<x[ii+1]<<";";
	}
	cout<<"];"<<endl;
	cout<<"delta_w=[";
	for(int ii=ini;ii<end;ii+=3)
	{
		cout<<x[ii+2]<<";";
	}
	cout<<"];"<<endl;
	*/
	/*
	ofstream out("C:/Users/Fernando/TrackingNuclei/matlabCode/OpticalFlow/temp/debugFuncGrad.txt");
	for(int ii=0;ii<3*numPartitions;ii+=3)
	{
		out<<mylib::Region_Volume(regionPvec[ii/3])<<" "<<x(ii+1)<<" "<<x(ii+2)<<" "<<x(ii+3)<<" "<<g(ii+1)<<" "<<g(ii+2)<<" "<<g(ii+3)<<endl;
	}
	out.close();
	exit(2);
	 */
	//-------------------------------------------------------------

	//--------------------------------------debug: write out gradient based on superpixels-----------------------------------------
	/*
	for(int numP=0;numP<numPartitions;numP++,numPpos+=3)
	{
		//regionP=mylib::Record_P_Vertex(imTargetPartition,numP,1,0);//regions can have holes
		regionP=regionPvec[numP];
		for (k = 0; k < regionP->rastlen; k+=2)
		{
			for (p = regionP->raster[k]; p <= regionP->raster[k+1]; p++)//perform computation on voxel p
			{
				cout<<p<<" "<<numP+1<<" "<<g(3*numP+1)<<" "<<g(3*numP+2)<<" "<<g(3*numP+3)<<";"<<endl;
			}
		}
		//mylib::Free_Region(regionP);
	}
	exit(2);
	 */
	//------------------------------------------------------------------------------------

#ifdef INTERP_TRILINEAR
	//if(xx!=NULL) delete[] xx;
	if(imTargetDer!=NULL) free(imTargetDer);
#endif
	//save to display iteration info
	glob_param->fData=f;
	glob_param->fSmooth=fSmooth;

	f+=lambda*fSmooth;

}


//================================================================================================
void calculateDataTermOneRegion(const mylib::Region* regionP,const mylib::Array* imTarget,const mylib::Array** imTargetDer,const mylib::uint16* imSourcePtr,const double* x,const float *scale,double deltaHdataTerm,double &fAux,double* gAux)
{

	//calculate data term (value and gradient) by exploring every voxel in the supervoxel
	/*
	 * TODO: calculate derivatives at any point using bicubic interpolation (it requires a lot of memory.Approx. x8 imageSize)
	 * If anytime you decide to implement it look at paper
	 * [1] F. Lekien and J. Marsden, ÒTricubic interpolation in three dimensions,Ó International Journal for Numerical Methods in Engineering, vol. 63, no. 3, pp. 455-471, May 2005.
	 * There is code at http://ece5470project.jasondsouza.org/programs that has been saved at ./interpolation
	 */

	mylib::Size_Type k;
	mylib::Indx_Type p;
	double auxI,der,f;
	int ndims=imTarget->ndims;
	mylib::Coordinate *c=mylib::Make_Array(mylib::PLAIN_KIND,mylib::DIMN_TYPE,1,&ndims);
	memset(gAux,0,sizeof(double)*(ndims));
	fAux=0.0;

#ifdef INTERP_TRILINEAR
	double* df=new double[ndims];
	double* xx=new double[ndims];
#endif
	
	



#if defined(INTERP_NEAREST_NEIGH)

	//nearest neighbor displacement (can be precomputed since it will be the same for each voxel in the same region)
	uvw= mylib::Indx_Type(x(numPpos+1))+imTarget->dims[0]*(mylib::Indx_Type(x(numPpos+2))+imTarget->dims[1]*mylib::Indx_Type(x(numPpos+3)));
	for (k = 0; k < regionP->rastlen; k+=2)
		for (p = regionP->raster[k]; p <= regionP->raster[k+1]; p++)//perform computation on voxel p
		{
			//nearest neighbor interpolation
			pTarget=max(mylib::Indx_Type(0),p+uvw);
			pTarget=min(pTarget,imTarget->size-1);
			auxI=(double)((imTargetPtr[pTarget]))-(double)((imSourcePtr[p]));
			HuberCostAndDer(auxI,deltaHdataTerm,fAux,gAux);
			f+=fAux;
			g(numPpos+1)+=gAux*imTarget_dxPtr[pTarget];//nearest neighbor interpolation of derivative
			g(numPpos+2)+=gAux*imTarget_dyPtr[pTarget];//nearest neighbor interpolation of derivative
			g(numPpos+3)+=gAux*imTarget_dzPtr[pTarget];//nearest neighbor interpolation of derivative

			//-----------------------------------debug------------------------------------------------------------
			/*
					 //cout<<pTarget<<" "<<p<<" "<<auxI<<" "<<fAux<<" "<<" "<<gAux<<" "<<imTarget_dxPtr[pTarget]<<" "<<imTarget_dyPtr[pTarget]<<" "<<imTarget_dzPtr[pTarget]<<";"<<endl;
					 mylib::Coordinate *tt=mylib::Idx2CoordA(imTarget,pTarget);
					 cout<<pTarget<<" "<<p<<" "<<auxI<<" "<<fAux<<" "<<" "<<gAux<<" "<<imTarget_dxPtr[pTarget]<<" "<<imTarget_dyPtr[pTarget]<<" "<<imTarget_dzPtr[pTarget]<<" ";
					 cout<<((mylib::Dimn_Type*)(tt->data))[0]+1<<" "<<((mylib::Dimn_Type*)(tt->data))[1]+1<<" "<<((mylib::Dimn_Type*)(tt->data))[2]+1<<";"<<endl;//matlab indexes
			 */
			//------------------------------------------------------
		}
#elif defined(INTERP_TRILINEAR)

	for (k = 0; k < regionP->rastlen; k+=2)
	{
		p = regionP->raster[k];
		mylib::Coordinate* coord=coord4idx(imTarget->ndims,imTarget->dims, p, "Hola");

		mylib::Dimn_Type* coordPtr=((mylib::Dimn_Type*)(coord->data));
		int dimIdx=0;
		for (; p <= regionP->raster[k+1]; p++)//perform computation on voxel p
		{
			for(int aa=0;aa<ndims;aa++) xx[aa]=coordPtr[aa]+x[aa];

			int err=trilinearInterpolation(xx,scale,imTarget,imTargetDer,auxI,df,c);//cubic interpolation
			if(err>0) exit(err);
			if(auxI<-1e31) continue;//flow out of bounds
			auxI-=(double)((imSourcePtr[p]));
			HuberCostAndDer(auxI,deltaHdataTerm,f,der);

			fAux+=f;
			for(int aa=0;aa<ndims;aa++) gAux[aa]+=der*df[aa];

			//increase coordinate counter
			dimIdx=0;
			while(dimIdx<ndims)
			{
				coordPtr[dimIdx]++;
				if(coordPtr[dimIdx]<imTarget->dims[dimIdx]) break;
				else coordPtr[dimIdx]=0;

				dimIdx++;
			}

			//-----------------------------------debug------------------------------------------------------------
			/*
						//cout<<pTarget<<" "<<p<<" "<<auxI<<" "<<fAux<<" "<<" "<<gAux<<" "<<imTarget_dxPtr[pTarget]<<" "<<imTarget_dyPtr[pTarget]<<" "<<imTarget_dzPtr[pTarget]<<";"<<endl;
						mylib::Coordinate *tt=mylib::Idx2CoordA(imTarget,pTarget);
						cout<<pTarget<<" "<<p<<" "<<auxI<<" "<<fAux<<" "<<" "<<gAux<<" "<<df[0]<<" "<<df[1]<<" "<<df[2]<<" ";
						cout<<((mylib::Dimn_Type*)(tt->data))[0]+1<<" "<<((mylib::Dimn_Type*)(tt->data))[1]+1<<" "<<((mylib::Dimn_Type*)(tt->data))[2]+1<<";"<<endl;//matlab indexes
			 */
			//------------------------------------------------------
		}
		mylib::Free_Array(coord);
	}

	delete[] xx;
	delete[] df;
#endif

	mylib::Free_Array(c);
}


//=================================================================================================
void calculateArrayGradient(mylib::Array *im,mylib::Array**& imGrad,const float* scale)
{
	mylib::Use_Reflective_Boundary();

	int ndims=im->ndims;
	if(imGrad!=NULL)
	{
		for(int ii=0;ii<ndims;ii++) mylib::Free_Array(imGrad[ii]);
	}else{
		//allocate memory for pointer
		imGrad=(mylib::Array**)malloc(ndims*sizeof(mylib::Array*));
	}

	//generate filter: 5 points centered finite difference
	mylib::Dimn_Type filterLength=5;


	//convert array to float to avoid losing precision
	//mylib::Array* imFloat=mylib::Convert_Image(im,im->kind,mylib::FLOAT32_TYPE,0);//scale value is ignored and values are scaled down by 2^16
	mylib::Array* imFloat=mylib::Make_Array(im->kind,mylib::FLOAT32_TYPE,im->ndims,im->dims);
	mylib::float32* imFloatPtr=(mylib::float32*)(imFloat->data);

	if(im->type!=mylib::UINT16_TYPE)
	{
		cout<<"ERROR: calculateArrayGradient: code not ready for images different than UINT16"<<endl;
		exit(2);
	}
	mylib::uint16* imPtr=(mylib::uint16*)(im->data);

	for(mylib::Size_Type ss=0;ss<imFloat->size;ss++) imFloatPtr[ss]=(mylib::float32)(imPtr[ss]);

	for(int ii=0;ii<ndims;ii++)
	{
		mylib::Double_Vector *filterDeriv=Make_Array(mylib::PLAIN_KIND,mylib::FLOAT64_TYPE,1,&filterLength);
		mylib::float64* filterDerivPtr=(mylib::float64*)filterDeriv->data;
		
		//using scale to calculate derivative
		filterDerivPtr[0]=1.0/(12.0*scale[ii]);filterDerivPtr[1]=-8.0/(12.0*scale[ii]);filterDerivPtr[2]=0.0;filterDerivPtr[3]=8.0/(12.0*scale[ii]);filterDerivPtr[4]=-1.0/(12.0*scale[ii]);
		
		imGrad[ii]=mylib::Copy_Array(imFloat);
		mylib::Filter_Dimension(imGrad[ii], filterDeriv, ii);//Filter_Dimension frees (consumes) the filterDeriv arrays
	}


	//------------------------debug--------------------------------
	/*
	//string pathImDer("/Users/amatf/TrackingNuclei/matlabCode/syntheticImages/");
	string pathImDer("C:/Users/Fernando/TrackingNuclei/matlabCode/syntheticImages/");
	cout<<"DEBUGGING: calculateArrayGradient: writing out image gradients in folder "<<pathImDer<<" with size ";
	for(int ii=0;ii<im->ndims;ii++) cout<<im->dims[ii]<<"x";
	cout<<endl;

	ofstream outBin;
	outBin.open(pathImDer+"imTarget_float.bin",ios::out | ios::binary);
	outBin.write((char*)(imFloat->data),sizeof(mylib::FLOAT32_TYPE)*(imFloat->size));
	outBin.close();
	outBin.open(pathImDer+"imTarget_dx.bin",ios::out | ios::binary);
	outBin.write((char*)(imGrad[0]->data),sizeof(mylib::FLOAT32_TYPE)*(imFloat->size));
	outBin.close();
	outBin.open(pathImDer+"imTarget_dy.bin",ios::out | ios::binary);
	outBin.write((char*)(imGrad[1]->data),sizeof(mylib::FLOAT32_TYPE)*(imFloat->size));
	outBin.close();
	outBin.open(pathImDer+"imTarget_dz.bin",ios::out | ios::binary);
	outBin.write((char*)(imGrad[2]->data),sizeof(mylib::FLOAT32_TYPE)*(imFloat->size));
	outBin.close();
	exit(2);
	 */
	//---------------------------------------------------------

	mylib::Free_Array(imFloat);
}

//=======================================================================================
int buildPyramidImageLabel_anisotropy5(mylib::Array *imgLabel, mylib::Array**& imgLabelPyramid,int numLabels,int*& numLabelsPyramid,int numLevels)
{
	mylib::Use_Reflective_Boundary();//for antia-aliasing filter
	int maxNumberUniqueLabels=27;//3x3x3 for antializsing filter
	mylib::uint32* countLabel=new mylib::uint32[maxNumberUniqueLabels];

	if(imgLabel->ndims!=3 && imgLabel->ndims!=2)
	{
		cout<<"ERROR: at buildPyramidImageLabel_anisotropy5: code is specialized on 3D images"<<endl;
		return 1;
	}
	if(imgLabel->type!=mylib::UINT32_TYPE)
	{
		cout<<"ERROR: at buildPyramidImageLabel_anisotropy5: code is not ready for imgLabels different than UINT32"<<endl;
		return 2;
	}

	if(imgLabelPyramid==NULL)
		imgLabelPyramid=(mylib::Array**)malloc(sizeof(mylib::Array*)*numLevels);

	if(numLabelsPyramid==NULL) numLabelsPyramid=new int[numLevels];

	//zero level is the current imgLabel
	imgLabelPyramid[0]=imgLabel;
	numLabelsPyramid[0]=numLabels;

	//build pyramid
	mylib::uint32* imTopPtr=NULL;
	mylib::Array* imTop=NULL;
	mylib::uint32* imBottomPtr=NULL;
	mylib::Array* imBottom=NULL;
	mylib::Dimn_Type* dimsVec=new mylib::Dimn_Type[imgLabel->ndims];
	mylib::Indx_Type posBottom=0,posTop=0;
	unsigned char* mapIdBool=new unsigned char[numLabels+1];//boolean indicator weather label is used in the foreground
	for(int ii=1;ii<numLevels;ii++)
	{
		imTop=imgLabelPyramid[ii-1];
		//allocate memory for new level in the pyramid
		for(int jj=0;jj<imgLabel->ndims;jj++)
		{
			if(jj==2 && ii<=2) dimsVec[jj]=imTop->dims[jj];//special case for anisotropy ~=5 where Z is not downsampled in the first two levels
			else{
				if(imTop->dims[jj]%2 == 0) dimsVec[jj]=imTop->dims[jj]/2;
				else dimsVec[jj]=(1+imTop->dims[jj])/2;
			}
		}
		imgLabelPyramid[ii]=mylib::Make_Array(imgLabel->kind,imgLabel->type,imgLabel->ndims,dimsVec);
		imBottom=imgLabelPyramid[ii];

		//data pointers
		imTopPtr=(mylib::uint32*)(imTop->data);
		imBottomPtr=(mylib::uint32*)(imBottom->data);

		//downsample
		posBottom=0;
		memset(mapIdBool,0,sizeof(unsigned char)*(numLabelsPyramid[ii-1]+1));//we assume label=0 is for background
		mylib::uint32 auxI;
		/*
		 * This piece of code does not use anti-aliasing.
		for(mylib::Dimn_Type zz=0;zz<imBottom->dims[2];zz++)
		{
			for(mylib::Dimn_Type yy=0;yy<imBottom->dims[1];yy++)
			{
				if(ii<=2)//no downsample in Z
					posTop=imTop->dims[0]*(2*yy+imTop->dims[1]*zz);
				else
					posTop=imTop->dims[0]*(yy+imTop->dims[1]*zz)*2;

				for(mylib::Dimn_Type xx=0;xx<imBottom->dims[0];xx++)
				{
					auxI=imTopPtr[posTop];
					imBottomPtr[posBottom]=auxI;
					mapIdBool[auxI]=1;//we assume label=0 is for background
					posBottom++;
					posTop+=2;
				}
			}
		}
		 */
		//downsample with anti-aliasing: given that th evalue of each pixel (=labels) does not have a physical meaning, we can not use the typical Gaussian smoothing. Otherwise we would get labels that are not there
		mylib::Frame* frame;
		if(imTop->ndims==3)
		{
			if(ii<=2)//no downsample in Z
				frame=mylib::Make_Frame(imTop,mylib::Coord3(1,3,3),mylib::Coord3(0,1,1));
			else
				frame=mylib::Make_Frame(imTop,mylib::Coord3(3,3,3),mylib::Coord3(1,1,1));
		}else{//2D
			frame=mylib::Make_Frame(imTop,mylib::Coord2(3,3),mylib::Coord2(1,1));
		}

		mylib::Place_Frame(frame,0);
		posBottom=0;
		int depth=1;
		if(imBottom->ndims==3) depth=imBottom->dims[2];
		for(mylib::Dimn_Type zz=0;zz<depth;zz++)
		{
			for(mylib::Dimn_Type yy=0;yy<imBottom->dims[1];yy++)
			{
				if(ii<=2)//no downsample in Z
					posTop=imTop->dims[0]*(2*yy+imTop->dims[1]*zz);
				else
					posTop=imTop->dims[0]*(yy+imTop->dims[1]*zz)*2;
				mylib::Place_Frame(frame,posTop);
				for(mylib::Dimn_Type xx=0;xx<imBottom->dims[0];xx++)
				{
					//calculate the label that appears more times within the frame (mode of the frame)
					auxI=mode(frame,countLabel,maxNumberUniqueLabels);
					imBottomPtr[posBottom]=auxI;
					mapIdBool[auxI]=1;
					mylib::Move_Frame_Forward(frame);
					mylib::Move_Frame_Forward(frame);
					posBottom++;
				}
			}
		}
		mylib::Free_Frame(frame);
		//remap labels to have consecutive id
		map<int,int>  mapId;
		int countL=0;
		for(int kk=1;kk<=numLabelsPyramid[ii-1];kk++)//we assume label=0 is for background
		{
			if(mapIdBool[kk]==1)
			{
				mapId[kk]=countL;
				countL++;
			}
		}

		//remap labels in the pyramid
		numLabelsPyramid[ii]=mapId.size();
		for(mylib::Size_Type kk=0;kk<imBottom->size;kk++)
		{
			if(imBottomPtr[kk]>0) imBottomPtr[kk]=mapId[imBottomPtr[kk]];
		}

		//make sure all the pixels with the same label form a 3^n-1 connected region

		int err=enforceLabelConnectivity(numLabelsPyramid[ii],imBottom);
		if(err>0) return err;

		cout<<"Pyramid Level "<<ii<<" has "<<numLabelsPyramid[ii]<<" labels"<<endl;

	}


	delete[] dimsVec;
	delete[] mapIdBool;
	delete[] countLabel;
	return 0;
}


//==================================================================================
//returns the mode of the frame
mylib::uint32 mode(mylib::Frame* frame,mylib::uint32* countLabel,int maxNumberUniqueLabels)
{
	map<mylib::uint32,int> hashLabel;//maps label values with a unique id value
	pair<map<mylib::uint32,int>::iterator,bool> it;

	int idCount=0;
	memset(countLabel,0,sizeof(mylib::uint32)*maxNumberUniqueLabels);

	mylib::uint32* data=NULL;
	if (mylib::Frame_Within_Array(frame))
	{
		mylib::Offs_Type *offs = mylib::Frame_Offsets(frame);//get pointer to offset
		data = ((mylib::uint32*)(mylib::AForm_Array(frame)->data)) + mylib::Frame_Index(frame);
		for (int i = 0; i < mylib::AForm_Size(frame); i++)
		{
			it=hashLabel.insert(pair<mylib::uint32,int>(data[offs[i]],idCount));
			if(it.second==true) idCount++;//new element

			countLabel[it.first->second]++;
		}
	}
	else
	{
		data = (mylib::uint32*) (mylib::Frame_Values(frame));
		for (int i = 0; i < mylib::AForm_Size(frame); i++)
		{
			it=hashLabel.insert(pair<mylib::uint32,int>(data[i],idCount));
			if(it.second==true) idCount++;//new element

			countLabel[it.first->second]++;
		}
	}

	//find mode
	mylib::uint32 mode=-1;
	int freq=0;
	map<mylib::uint32,int>::const_iterator iter=hashLabel.begin();
	for(int ii=0;ii<idCount;ii++)
	{
		if(freq<countLabel[iter->second])
		{
			freq=countLabel[iter->second];
			mode=iter->first;
		}
		++iter;
	}

	return mode;
}


//=====================================================================
void newiteration_residOpticalFlow(int iter, const double* x, double f,const double* g, void *params)
{
	globs_LBFGS_ *glob_param=(globs_LBFGS_*)params;
	double normG=0.0;
	int N=glob_param->imSource->ndims*mylib::Get_Partition_Vertex_Count(glob_param->imTargetPartition);
	for(int ii=0;ii<N;ii++) normG+=g[ii]*g[ii];
	normG=sqrt(normG);
	cout<<"Iter="<<iter<<";fData="<<glob_param->fData<<";fSmooth="<<glob_param->fSmooth<<";fData+lambda*fSmooth="<<f<<";RMS(g)="<<normG/((double)N)<<endl;
	//cout<<x[0]<<" "<<x[1]<<" "<<x[2]<<endl;
}



//interpolates img at point x using N one-dimensional cubic polynomials
//if df is not null it also computes the one-directional derivatives
int nCubicInterpolation(const double *x,const float* scale,mylib::Array *img,mylib::Array** img_der,double &f,double *df)
{
	if(img->type!=mylib::UINT16_TYPE)
	{
		cout<<"ERROR: nCubicInterpolation: code is only ready for UINT16 img type"<<endl;
		return 2;
	}

	mylib::uint16* imgPtr=(mylib::uint16*)(img->data);

	//setup floor coordinate
	mylib::Coordinate *c=mylib::Make_Array(mylib::PLAIN_KIND,mylib::DIMN_TYPE,img->ndims,img->dims);
	mylib::Dimn_Type* cPtr=(mylib::Dimn_Type*)(c->data);
	mylib::Indx_Type cc=0,offset=1;
	for(mylib::Dimn_Type d=0;d<img->ndims;d++)
	{
		cPtr[d]=(mylib::Dimn_Type)floor(x[d]);
		if(cPtr[d]<0 || cPtr[d]>=img->dims[d])//we can not interpolate outside the image
		{
			f=-1e32;//equivalent to NaN
			for(mylib::Dimn_Type aa=0;aa<img->ndims;aa++) df[d]=-1e32;
			mylib::Free_Array(c);
			return 0;
			//cout<<"ERROR: nCubicInterpolation: interpolation location is out of image bounds"<<endl;
			//return 1;
		}
		cc+=(cPtr[d]*offset);//center
		offset*=img->dims[d];
	}



	//calculate cubic polynomial for each dimension
	offset=1;
	double a0,a1,a2,a3,mu,mu2,y1,y2;
	double b1,b2;
	//double y0,y3;//uncomment if we use standard cubic interpoaltion
	mylib::float32* img_derPtr=NULL;//uncomment if we use Catmull-Rom
	f=0;//initialize final value
	for(mylib::Dimn_Type d=0;d<img->ndims;d++)
	{
		if(img_der[d]->type!=mylib::FLOAT32_TYPE)
		{
			cout<<"ERROR: nCubicInterpolation: code is only ready for FLOAT32 img derivatives type"<<endl;
			return 3;
		}
		img_derPtr=(mylib::float32*)(img_der[d]->data);

		if(cPtr[d]==0)//special case close to the boundary
		{
			//symmetric boundaries
			y1=(double)(imgPtr[cc])-f;//a simple offset does not affect the derivative value
			//y0=y1;
			y2=(double)(imgPtr[cc+offset])-f;
			//y3=(double)(imgPtr[cc+offset+offset])-f;
			b1=(double)(img_derPtr[cc]);
			b2=(double)(img_derPtr[cc+offset]);
		}else if(cPtr[d]==img->dims[d]-2){//special case close to the boundary
			//y0=(double)(imgPtr[cc-offset])-f;//a simple offset does not affect the derivative value
			y1=(double)(imgPtr[cc])-f;//this is the value for mu=0
			y2=(double)(imgPtr[cc+offset])-f;
			//y3=y2;
			b1=(double)(img_derPtr[cc]);
			b2=(double)(img_derPtr[cc+offset]);
		}else if(cPtr[d]==img->dims[d]-1){//special case close to the boundary
			//y0=(double)(imgPtr[cc-offset])-f;//a simple offset does not affect the derivative value
			y1=(double)(imgPtr[cc])-f;//this is the value for mu=0
			y2=y1;
			//y3=y0;
			b1=(double)(img_derPtr[cc]);
			b2=b1;
		}
		else{//inbounds
			//y0=(double)(imgPtr[cc-offset])-f;//a simple offset does not affect the derivative value
			y1=(double)(imgPtr[cc])-f;//this is the value for mu=0
			y2=(double)(imgPtr[cc+offset])-f;
			//y3=(double)(imgPtr[cc+offset+offset])-f;
			b1=(double)(img_derPtr[cc]);
			b2=(double)(img_derPtr[cc+offset]);
		}

		//standard cubic interpolation: polynomial passes through x={-1 0 1 2}. Derivatives are not that great
		/*
		a0 = (-y0+3.0*(y1-y2)+y3)/6.0;
		a1 = (y0-2.0*y1+y2)/2.0;
		a2 = (-2.0*y0-3.0*y1+6.0*y2-y3)/6.0;
		a3 = y1;
		//REMEMBER TO ACCOUNT FOR ANYSOTROPY IF USING THIS METHOD
		 */

		//Catmull-Rom cubic interpolation: the spline passes through x={0,1} and agrees with finite differences derivatives at x={0,1}
		mu=(y1-y2);//intermediate computations
		mu2=b1+b2;
		a0 = 2.0*mu+mu2;
		a1 = -3.0*mu-b1-mu2;
		a2 = b1;
		a3 = y1;

		mu=x[d]-(double)(cPtr[d]);//value between 0 and 1
		mu2=mu;
		f+=a0*mu*mu2+a1*mu2+a2*mu+a3;//anisotropy does not affect value (only its derivatives)

		//if(df!=NULL) df[d]=(3.0*a0*mu2+2.0*mu*a1+a2)/scale[d];//to account for anisotropy
		if(df!=NULL) df[d]=(3.0*a0*mu2+2.0*mu*a1+a2);//if we use Catmull-Rom we should have accounted for anisotropy already in the derivatives b1 and b2

		offset*=img->dims[d];
	}

	mylib::Free_Array(c);

	return 0;
}

//=============================================================================================================
//trilinear interpolation for image intensity and derivatives calculated from finite differences)
int trilinearInterpolation(const double *x,const float* scale,const mylib::Array *img,const mylib::Array** img_der,double &f,double *df,mylib::Coordinate* c)
{
	if(img->type!=mylib::UINT16_TYPE)
	{
		cout<<"ERROR: trilinearInterpolation: code is only ready for UINT16 img type"<<endl;
		return 2;
	}

	mylib::uint16* imgPtr=(mylib::uint16*)(img->data);

	//setup floor coordinate
	//mylib::Coordinate *c=mylib::Make_Array(mylib::PLAIN_KIND,mylib::DIMN_TYPE,1,&(img->ndims));
	mylib::Dimn_Type* cPtr=(mylib::Dimn_Type*)(c->data);
	mylib::Indx_Type cc=0,offset=1;
	for(mylib::Dimn_Type d=0;d<img->ndims;d++)
	{
		cPtr[d]=(mylib::Dimn_Type)floor(x[d]);
		if(cPtr[d]<0 || cPtr[d]>=(img->dims[d]-1))//we can not interpolate outside the image (we need at least one voxel)
		{
			f=-1e32;//equivalent to NaN
			for(mylib::Dimn_Type aa=0;aa<img->ndims;aa++) df[d]=-1e32;
			//mylib::Free_Array(c);
			return 0;
			//cout<<"ERROR: trilinearInterpolation: interpolation location is out of image bounds"<<endl;
			//return 1;
		}
		cc+=(cPtr[d]*offset);//(0,0,0) point in the cube
		offset*=img->dims[d];
	}
	int ndims=img->ndims;
	//calculate each corner of the cube to interpolate on and its weight
	mylib::Indx_Type sz=img->dims[0]*img->dims[1];
	mylib::Indx_Type pCube[8];
	double wCubeFloor[3],wCubeCeil[3];
	pCube[0]=cc;pCube[1]=cc+1;pCube[2]=pCube[1]+img->dims[0];pCube[3]=pCube[0]+img->dims[0];
	pCube[4]=pCube[0]+sz;pCube[5]=pCube[1]+sz;pCube[6]=pCube[2]+sz;pCube[7]=pCube[3]+sz;//guaranteed to be within boundaries

	wCubeFloor[0]=x[0]-(double)(cPtr[0]);//value between 0 and 1
	wCubeFloor[1]=x[1]-(double)(cPtr[1]);//value between 0 and 1
	
	wCubeCeil[0]=1.0-wCubeFloor[0];
	wCubeCeil[1]=1.0-wCubeFloor[1];
	
	if(ndims>2)
	{
		wCubeFloor[2]=x[2]-(double)(cPtr[2]);//value between 0 and 1
		wCubeCeil[2]=1.0-wCubeFloor[2];
	}

	if(ndims>2)
	f=wCubeCeil[2] *( wCubeCeil[1] *(wCubeCeil[0] *imgPtr[pCube[0]]+wCubeFloor[0]*imgPtr[pCube[1]]) +
			wCubeFloor[1]*(wCubeFloor[0]*imgPtr[pCube[2]]+wCubeCeil[0] *imgPtr[pCube[3]]) )+
			wCubeFloor[2]*( wCubeCeil[1] *(wCubeCeil[0] *imgPtr[pCube[4]]+wCubeFloor[0]*imgPtr[pCube[5]]) +
					wCubeFloor[1]*(wCubeFloor[0]*imgPtr[pCube[6]]+wCubeCeil[0] *imgPtr[pCube[7]]) );
	else
		f=	wCubeCeil[1] *(wCubeCeil[0] *imgPtr[pCube[0]]+wCubeFloor[0]*imgPtr[pCube[1]]) +
			wCubeFloor[1]*(wCubeFloor[0]*imgPtr[pCube[2]]+wCubeCeil[0] *imgPtr[pCube[3]]);
			
	if(df!=NULL)
	{
		mylib::float32* img_derPtr=NULL;
		for(mylib::Dimn_Type d=0;d<img->ndims;d++)
		{
			if(img_der[d]->type!=mylib::FLOAT32_TYPE)
			{
				cout<<"ERROR: nCubicInterpolation: code is only ready for FLOAT32 img derivatives type"<<endl;
				return 3;
			}
			img_derPtr=(mylib::float32*)(img_der[d]->data);

			if(ndims>2)
				df[d]=	wCubeCeil[2] *( wCubeCeil[1] *(wCubeCeil[0] *img_derPtr[pCube[0]]+wCubeFloor[0]*img_derPtr[pCube[1]]) +
										wCubeFloor[1]*(wCubeFloor[0]*img_derPtr[pCube[2]]+wCubeCeil[0] *img_derPtr[pCube[3]]) )+
						wCubeFloor[2]*( wCubeCeil[1] *(wCubeCeil[0] *img_derPtr[pCube[4]]+wCubeFloor[0]*img_derPtr[pCube[5]]) +
										wCubeFloor[1]*(wCubeFloor[0]*img_derPtr[pCube[6]]+wCubeCeil[0] *img_derPtr[pCube[7]]) );
			else
				df[d]=	wCubeCeil[1] *(wCubeCeil[0] *img_derPtr[pCube[0]]+wCubeFloor[0]*img_derPtr[pCube[1]]) +
						wCubeFloor[1]*(wCubeFloor[0]*img_derPtr[pCube[2]]+wCubeCeil[0] *img_derPtr[pCube[3]]);
		}
	}

	//mylib::Free_Array(c);

	return 0;
}

//================================================================================================================
mylib::Array* getFlowArrayFromRegions(mylib::Partition* imgPartition,const vector<flow3D>& flow)
{
	mylib::Array* flowArray=NULL;
	int numPartitions=mylib::Get_Partition_Vertex_Count(imgPartition);
	if(numPartitions!=(int)(flow.size()))
	{
		cout<<"ERROR: getFowArrayFromRegions: number of labels in partition is different than flow elements"<<endl;
		return flowArray;
	}

	mylib::Array* img=(mylib::Array*)(mylib::Get_Partition_APart(imgPartition));
	flowArray=mylib::Make_Array(mylib::RGB_KIND,mylib::FLOAT32_TYPE,img->ndims,img->dims);//each channel saves one flow direction

	//get RGB planes
	mylib::Array_Bundle red, green, blue;
	red = green = blue = *flowArray;
	Get_Array_Plane(&red,0);
	Get_Array_Plane(&green,1);
	Get_Array_Plane(&blue,2);
	mylib::float32* redPtr=(mylib::float32*)(red.data);
	mylib::float32* bluePtr=(mylib::float32*)(blue.data);
	mylib::float32* greenPtr=(mylib::float32*)(green.data);


	//set all elements to zero
	memset((char*)(flowArray->data),0,sizeof(mylib::float32)*flowArray->size);

	flow3D auxF;
	mylib::Region *regionP=NULL;
	for(int numP=0;numP<numPartitions;numP++)
	{
		auxF.x=flow[numP].x;auxF.y=flow[numP].y;auxF.z=flow[numP].z;
		regionP=mylib::Record_P_Vertex(imgPartition,numP,1,0);//regions can have holes
		for (int k = 0; k < regionP->rastlen; k+=2)
		{
			for (mylib::Indx_Type p = regionP->raster[k]; p <= regionP->raster[k+1]; p++)//perform computation on voxel p
			{
				redPtr[p]=auxF.x;
				greenPtr[p]=auxF.y;
				bluePtr[p]=auxF.z;
			}
		}
		mylib::Free_Region(regionP);
	}
	return flowArray;
}


//================================================================================
int getFlowRegionsFromFlowArray_anisotropy5(mylib::Array* flowArray,mylib::Partition* imgLabelPartition,int pyramidLevel,vector<flow3D>& flow,const vector<vector<pair<int,double> > >& partitionNeighbors)
{
	int numPartitions=mylib::Get_Partition_Vertex_Count(imgLabelPartition);
	flow.resize(numPartitions);

	if(flowArray==NULL)//just set flow to zero an return
	{
		for(int ii=0;ii<numPartitions;ii++) flow[ii].reset();
		return 0;
	}

	//get RGB planes
	if(flowArray->kind!=mylib::RGB_KIND)
	{
		cout<<"ERROR: getFlowRegionsFromFlowArray_anisotropy5: flowArray must be RGB_KIND"<<endl;
		return 1;
	}
	if(flowArray->type!=mylib::FLOAT32_TYPE)
	{
		cout<<"ERROR: getFlowRegionsFromFlowArray_anisotropy5: flowArray must be FLOAT32_TYPE"<<endl;
		return 2;
	}

	mylib::Array_Bundle red, green, blue;
	red = green = blue = *flowArray;
	Get_Array_Plane(&red,0);
	Get_Array_Plane(&green,1);
	Get_Array_Plane(&blue,2);
	mylib::float32* redPtr=(mylib::float32*)(red.data);
	mylib::float32* bluePtr=(mylib::float32*)(blue.data);
	mylib::float32* greenPtr=(mylib::float32*)(green.data);

	mylib::Indx_Type dz=2;
	if(pyramidLevel<=2) dz=1;//only x,y are downsampled

	mylib::Use_Array_Basis((mylib::Array*)(mylib::Get_Partition_APart(imgLabelPartition)));//to determine coordinates
	mylib::Region* regionP=NULL;
	mylib::Coordinate* coord=NULL;
	mylib::Dimn_Type* coordPtr=NULL;
	mylib::Indx_Type pFlow;
	flow3D auxF;//initiliaze at zero
	int flag=0;
	int ndims=((mylib::Array*)(mylib::Get_Partition_APart(imgLabelPartition)))->ndims;
	int countR=0,countG=0,countB=0;
	for(int numP=0;numP<numPartitions;numP++)
	{
		regionP=mylib::Record_P_Vertex(imgLabelPartition,numP,1,0);//regions can have holes
		auxF.reset();
		countR=0;countG=0;countB=0;
		for (int k = 0; k < regionP->rastlen; k+=2)
		{
			coord=mylib::Idx2Coord(regionP->raster[k]);
			coordPtr=(mylib::Dimn_Type*)(coord->data);
			if(ndims==3)
				pFlow=(coordPtr[0]/2)+flowArray->dims[0]*((coordPtr[1]/2)+flowArray->dims[1]*(coordPtr[2]/dz));//x.5 is rounded to x
			else
				pFlow=(coordPtr[0]/2)+flowArray->dims[0]*(coordPtr[1]/2);//x.5 is rounded to x
			if((coordPtr[0]%2)==1) flag=0;
			else flag=1;
			for (mylib::Indx_Type p = regionP->raster[k]; p <= regionP->raster[k+1]; p++)//perform computation on voxel p
			{
				if(fabs(redPtr[pFlow])>1e-3)
				{
					auxF.x+=redPtr[pFlow];
					countR++;
				}
				if(fabs(greenPtr[pFlow])>1e-3)
				{
					auxF.y+=greenPtr[pFlow];
					countG++;
				}
				if(fabs(bluePtr[pFlow])>1e-3)
				{
					auxF.z+=bluePtr[pFlow];
					countB++;
				}
				//-------------------debug---------------------------
				/*
				if(pFlow>=blue.size)
				{
					cout<<"ERROR: accessing outside flow level"<<endl;
					exit(2);
				}
				 */
				//------------------------------------------------
				flag^=1;//XOR operation: flag alternates between 0 and 1 so pFlow is only updated every other pass
				pFlow+=flag;
			}
			mylib::Free_Array(coord);
		}

		double auxV=(double)(mylib::Region_Volume(regionP));
		if(auxV<0.5) auxV=2.0;//to avoid division by zero

		if(countR>0) flow[numP].x=2.0*auxF.x/((double)countR);//we assign the average flow to the new region (disregarding blank spaces) multiplied by 2 (to account for downsampling)
		else flow[numP].x=-1e32;
		if(countG>0) flow[numP].y=2.0*auxF.y/((double)countG);//we assign the average flow to the new region
		else flow[numP].y=0.0;
		if(countB>0) flow[numP].z=dz*auxF.z/((double)countB);//we assign the average flow to the new region
		else flow[numP].z=0.0;

		mylib::Free_Region(regionP);
	}


	//explore all the graph to generate weighted average in regions where we did not get unformation.
	//TODO: some super-voxels still get zero flow assigned
	double *wTotal=new double[numPartitions];
	flow3D *flowAvg=new flow3D[numPartitions];
	bool noZeros=false;
	
	for(int numP=0;numP<numPartitions;numP++) flowAvg[numP].reset();
	memset(wTotal,0,sizeof(double)*numPartitions);
	int pos;
	double ww;
	for(unsigned int ii=0;ii<partitionNeighbors.size();ii++)
	{ 
		for(unsigned int jj=0;jj<partitionNeighbors[ii].size();jj++)
		{
			pos=partitionNeighbors[ii][jj].first;
			ww=partitionNeighbors[ii][jj].second;
			if(flow[ii].x>-1e32)
			{
				wTotal[pos]+=ww;
				flowAvg[pos].x+=ww*flow[ii].x;flowAvg[pos].y+=ww*flow[ii].y;flowAvg[pos].z+=ww*flow[ii].z;
			}
			if(flow[pos].x>-1e32)
			{
				wTotal[ii]+=ww;
				flowAvg[ii].x+=ww*flow[pos].x;flowAvg[ii].y+=ww*flow[pos].y;flowAvg[ii].z+=ww*flow[pos].z;
			}
		}
	}

	//fill in the flows that were not hit from the lower upper of teh pyramid using the MRF
	for(int numP=0;numP<numPartitions;numP++)
	{
		if(flow[numP].x>-1e30) continue;

		if(wTotal[numP]<1e-3)
		{
			flow[numP].x=0.0;
			//cout<<numP<<"still zero "<<endl;
		}
		else{
			flow[numP].x=flowAvg[numP].x/wTotal[numP];
			flow[numP].y=flowAvg[numP].y/wTotal[numP];
			flow[numP].z=flowAvg[numP].z/wTotal[numP];
		}
	}
	
	delete[] wTotal;
	delete[] flowAvg;
	

	return 0;
}



//=======================================================================================================
void writeFlowFromPartition(ostream &out,mylib::Partition* imgLabelPartition,const vector<flow3D>& flow)
{
	cout<<"DEBUGING: writeFlowFromPartition: writing out flow"<<endl;
	int numPartitions=mylib::Get_Partition_Vertex_Count(imgLabelPartition);
	for(int numP=0;numP<numPartitions;numP++)
	{
		mylib::Region *regionP=mylib::Record_P_Vertex(imgLabelPartition,numP,1,0);//regions can have holes
		for (int k = 0; k < regionP->rastlen; k+=2)
		{
			for (mylib::Indx_Type p = regionP->raster[k]; p <= regionP->raster[k+1]; p++)//perform computation on voxel p
			{
				out<<p<<" "<<numP+1<<" "<<flow[numP].x<<" "<<flow[numP].y<<" "<<flow[numP].z<<";"<<endl;
			}
		}
		mylib::Free_Region(regionP);
	}
	//exit(2);
}

//=========================================================================================================
void partitionNeighbors(mylib::Partition* imgLabelPartition, float maxDistance,const float* scale, vector<vector<pair<int,double> > >& partitionNeigh,float &maxVolume)
{
	//at dist=sigma -> the wieght is ~1/3 of the weight at distance=0
	float sigma=maxDistance/2.0f;//to calculate the weights for each smoothing term based on a Lorentzian of the distance
	int numPartitions=mylib::Get_Partition_Vertex_Count(imgLabelPartition);
	mylib::Dimn_Type N=((mylib::Array*)(mylib::Get_Partition_APart(imgLabelPartition)))->ndims;
	float maxDistance2=maxDistance*maxDistance;//to avoid sqrt()

	//calculate centroid for each region and its volume
	float* centroids=new float[N*numPartitions];//[x_0,y_0,z_0,...,x_N,y_N,z_N]
	maxVolume=0;
	float* regionVol=new float[numPartitions];

	mylib::Use_Array_Basis((mylib::Array*)(mylib::Get_Partition_APart(imgLabelPartition)));//to determine coordinates
	mylib::Region* regionP=NULL;
	mylib::Coordinate* coord=NULL;
	mylib::Dimn_Type* coordPtr=NULL;
	float *auxF=new float[N];
	long int count=0;
	for(int numP=0;numP<numPartitions;numP++)
	{
		regionP=mylib::Record_P_Vertex(imgLabelPartition,numP,1,0);//regions can have holes. TODO: use the precomputed regionPvec to save some time
		memset(auxF,0,sizeof(float)*N);
		for (int k = 0; k < regionP->rastlen; k+=2)
		{
			coord=mylib::Idx2Coord(regionP->raster[k]);
			coordPtr=(mylib::Dimn_Type*)(coord->data);
			for (mylib::Indx_Type p = regionP->raster[k]; p <= regionP->raster[k+1]; p++)//perform computation on voxel p
			{
				for(int ii=0;ii<N;ii++) auxF[ii]+=coordPtr[ii];

				coordPtr[0]++;//to avoid doing Idx2Coord all the time
			}
			mylib::Free_Array(coord);
		}
		float auxV=(float)(mylib::Region_Volume(regionP));
		if(auxV<0.5)//empty region
		{
			auxV=0;
			for(int ii=0;ii<N;ii++)
			{
				centroids[count++]=-1e32;
			}
		}else{
			for(int ii=0;ii<N;ii++)
			{
				centroids[count++]=scale[ii]*auxF[ii]/auxV;
			}
		}
		if(auxV>maxVolume) maxVolume=auxV;//save maximum volume to normalize weights later on
		regionVol[numP]=auxV;
	}

	//reset partition list
	partitionNeigh.resize(numPartitions);
	for(int ii=0;ii<numPartitions;ii++) partitionNeigh[ii].clear();

	//brute force search: we only count edges once
	count=0;
	float dist=0,auxD=0;
	long int count2=0;
	bool flag=false;
	double w;
	for(int ii=0;ii<numPartitions;ii++)
	{
		for(int kk=0;kk<N;kk++) auxF[kk]=centroids[count++];

		count2=(ii+1)*N;
		for(int jj=ii+1;jj<numPartitions;jj++)
		{
			dist=0;
			flag=false;
			for(int kk=0;kk<N;kk++)
			{
				auxD=auxF[kk]-centroids[count2+kk];
				dist+=auxD*auxD;
				if(dist>maxDistance2)
				{
					flag=true;
					break;
				}
			}
			count2+=N;
			if(flag==false && dist<maxDistance2)
			{
				w=exp(-sqrt(dist)/sigma)*((regionVol[ii]+regionVol[jj])/(maxVolume));//weight based on distance and volume of the regions
				partitionNeigh[ii].push_back(pair<int,double>(jj,w));
			}
		}
	}


	//---------------------------------debug---------------------------------
	/*
	for(unsigned int ii=0;ii<partitionNeigh.size();ii++)
	{
		cout<<"Region "<<ii<<" has neigh:";

		for(unsigned int jj=0;jj<partitionNeigh[ii].size();jj++)
		{
			cout<<partitionNeigh[ii][jj].first<<" ";
		}

		//cout<<partitionNeigh[ii].size();
		cout<<endl;
		exit(2);
	}
	 */
	//----------------------------------------------------------------------

	//release memory
	delete[] centroids;
	delete[] auxF;
	delete[] regionVol;
}


//================================================================
int enforceLabelConnectivity(int& numlabels,mylib::Array *imgLabels)
{
	if(imgLabels->type!=mylib::UINT32_TYPE)
	{
		cout<<"ERROR: enforceLabelConnectivity: image labels array has to be UINT32"<<endl;
		return 1;
	}

	mylib::uint32* imgLabelsPtr=(mylib::uint32*)(imgLabels->data);
	mylib::uint32 countL=0;//background is considered 0
	unsigned char* map=new unsigned char[imgLabels->size];
	memset(map,0,imgLabels->size);//reset to zero

	FloodFillArgs floodFillArgs;
	floodFillArgs.imgLabelsPtr=imgLabelsPtr;
	floodFillArgs.map=map;

	//I need to offset all the label values to make sure we do not create confusion in the in-place floodfill in the next step
	mylib::uint32 offset=numlabels+10;
	for(mylib::Indx_Type p=0;p<imgLabels->size;p++)
	{
		if(imgLabelsPtr[p]>offset)
		{
			cout<<"ERROR: enforceLabelConnectivity: maximum value is larger than numlabels. It violates our assumptions"<<endl;
			return 4;
		}
		if(imgLabelsPtr[p]>0)//it is foregound pixel
		{
			imgLabelsPtr[p]+=offset;
		}
	}

	//flood fill each connected region and assign a label value
	for(mylib::Indx_Type p=0;p<imgLabels->size;p++)
	{
		if(map[p]==0 && imgLabelsPtr[p]>0)//pixel has not been visited and it is foregound;map is updated by actFF routine
		{
			countL++;
			floodFillArgs.label=imgLabelsPtr[p];
			floodFillArgs.countL=countL;
			mylib::Flood_Object(imgLabels,0,0,p,&floodFillArgs,testFF,NULL,NULL,NULL,NULL,&floodFillArgs,actFF);
		}
	}

	//-----------------------debug: write out img label field-------------------------
	/*
	cout<<"DEBUGGING: enforceLabelConnectivity: writing oout image label field"<<endl;
	 ofstream outBin;
	outBin.open("/Users/amatf/Downloads/testMylib2/testLabelsUINT32.bin",ios::binary | ios::out);
	 outBin.write((char*)imgLabelsPtr,sizeof(mylib::uint32)*(imgLabels->size));
	outBin.close();
	exit(2);
	 */
	//----------------------------------------------------------------------------------
	numlabels=countL;
	delete[] map;
	return 0;
}


//==========================================================================================================================
int calculateEnergyOpticalFlow(mylib::Array *flowArray,globs_LBFGS_ *glob_param)
{
	cout<<"DEBUGGING: calculateEnergyOpticalFlow"<<endl;
	//copied from funcgrad_opticalFlow

	mylib::Partition* imTargetPartition=glob_param->imTargetPartition;
	mylib::Array* imSource=glob_param->imSource;
	mylib::Array* imTarget=glob_param->imTarget;
	vector<vector<pair<int,double> > >* partitionNeigh=glob_param->partitionNeigh;
	mylib::Region** regionPvec=glob_param->regionPvec;

	double lambda=glob_param->lambda;
	double deltaHsmoothTerm=glob_param->deltaHsmoothTerm;
	double deltaHdataTerm=glob_param->deltaHdataTerm;
	float* scale=glob_param->scale;

	if(imSource->type!=mylib::UINT16_TYPE || imTarget->type!=mylib::UINT16_TYPE)
	{
		cout<<"ERROR: calculateEnergyOpticalFlow: code expects UINT16 images"<<endl;
		exit(2);
	}
	if(flowArray->type!=mylib::FLOAT32_TYPE)
	{
		cout<<"ERROR: calculateEnergyOpticalFlow: code expects FLOAT32 arrays"<<endl;
		exit(2);
	}

	mylib::float32* flowArrayPtr=(mylib::float32*)(flowArray->data);
	mylib::uint16* imSourcePtr=(mylib::uint16*)(imSource->data);

	//initialize residual and gradient
	int numPartitions=mylib::Get_Partition_Vertex_Count(imTargetPartition);
	double f=0;
	double fSmooth=0;

	ap::real_1d_array x,g;
	x.setbounds(1,3*numPartitions);
	g.setbounds(1,3*numPartitions);
	memset(g.getcontent(),0,sizeof(double)*3*numPartitions);
	memset(x.getcontent(),0,sizeof(double)*3*numPartitions);


	//calculate derivatives in the grid;
	mylib::Array** imGrad=NULL;
	calculateArrayGradient(imTarget,imGrad,scale);
	mylib::Array* imTarget_dx=imGrad[0];
	mylib::Array* imTarget_dy=imGrad[1];
	mylib::Array* imTarget_dz=imGrad[2];


	int numPpos=0,numNeighPos=0;
	//int *listedges=NULL;
	//int nedges=0;;
	double fAux,gAux,auxU,auxV,auxW;
	int ndims=imTarget->ndims;
	mylib::Indx_Type p;

#ifdef INTERP_TRILINEAR
	const mylib::Array** imTargetDer=(const mylib::Array**)malloc(sizeof(mylib::Array*)*imTarget->ndims);

	imTargetDer[0]=imTarget_dx;imTargetDer[1]=imTarget_dy;imTargetDer[2]=imTarget_dz;
#endif
	mylib::Use_Array_Basis(imTarget);//set basis to get all the coordinate indexes

	
	float *fDataVec=new float[numPartitions];
	float *fSmoothVec=new float[numPartitions];
	memset(fDataVec,0,sizeof(float)*numPartitions);
	memset(fSmoothVec,0,sizeof(float)*numPartitions);
	mylib::Region* regionP=NULL;


	if(regionPvec==NULL)
	{
		regionPvec=(mylib::Region**)malloc(sizeof(mylib::Region*)*numPartitions);
		for(int numP=0;numP<numPartitions;numP++)
			regionPvec[numP]=mylib::Record_P_Vertex(imTargetPartition,numP,1,0);
	}

	

	//single thread execution
	for(int numP=0;numP<numPartitions;numP++)
	{
		numPpos=numP*ndims;//for parallelization
		regionP=regionPvec[numP];
		
		//fill in correct x value
		p = regionP->raster[0];
		for(int ii=0;ii<ndims;ii++)
		{
			x(numPpos+1+ii)=flowArrayPtr[p+ii*imTarget->size];
		}

		calculateDataTermOneRegion(regionPvec[numP],imTarget,imTargetDer,imSourcePtr,x.getcontent()+numPpos,scale,deltaHdataTerm,fAux,g.getcontent()+numPpos);
		f+=fAux;
		
		fDataVec[numP]=fAux;
	}
	

	

	//calculate smooth term (value and gradient) using regions closer than maxDistance (precalculated ahead of time)
	numPpos=0;
	int *numNeighbors=new int[numPartitions];
	memset(numNeighbors,0,sizeof(int)*numPartitions);
	for(int numP=0;numP<numPartitions;numP++,numPpos+=3)
	{
		vector<pair<int,double> >* edges=&((*partitionNeigh)[numP]);
		double lambdaAux;
		for(vector<pair<int,double> >::const_iterator iter=edges->begin();iter!=edges->end();++iter)//we do not need to avoid double counting because it was already done during neighbor list creation
		{
			numNeighPos=3*(iter->first);
			lambdaAux=lambda*(iter->second);
			auxU=(x(numPpos+1)-x(numNeighPos+1))*scale[0];
			auxV=(x(numPpos+2)-x(numNeighPos+2))*scale[1];
			auxW=(x(numPpos+3)-x(numNeighPos+3))*scale[2];
			HuberCostAndDer(auxU,deltaHsmoothTerm,fAux,gAux);
			fSmooth+=fAux*(iter->second);
			fSmoothVec[numP]+=fAux*(iter->second);
			fSmoothVec[iter->first]+=fAux*(iter->second);
			g(numPpos+1)+=lambdaAux*gAux;//*1.0
			g(numNeighPos+1)-=lambdaAux*gAux;
			numNeighbors[numP]++;
			numNeighbors[iter->first]++;

			HuberCostAndDer(auxV,deltaHsmoothTerm,fAux,gAux);
			fSmooth+=fAux*(iter->second);
			fSmoothVec[numP]+=fAux*(iter->second);
			fSmoothVec[iter->first]+=fAux*(iter->second);
			g(numPpos+2)+=lambdaAux*gAux;//*1.0
			g(numNeighPos+2)-=lambdaAux*gAux;

			HuberCostAndDer(auxW,deltaHsmoothTerm,fAux,gAux);
			fSmooth+=fAux*(iter->second);
			fSmoothVec[numP]+=fAux*(iter->second);
			fSmoothVec[iter->first]+=fAux*(iter->second);
			g(numPpos+3)+=lambdaAux*gAux;//*1.0
			g(numNeighPos+3)-=lambdaAux*gAux;
		}
	}

	

#ifdef INTERP_TRILINEAR
	//if(xx!=NULL) delete[] xx;
	if(imTargetDer!=NULL) free(imTargetDer);
#endif
	

	cout<<"fData="<<f<<";fSmooth="<<fSmooth<<";fData+lambda*fSmooth="<<f+lambda*fSmooth<<endl;
	f+=lambda*fSmooth;



	//-------------------------------------------------debug--------------------------------------------
	/*
	//write out residual for each region in the partition
	cout<<"DEBUGGING: writing out optical flow residual for each region"<<endl;
	ofstream outResid("opticalFlowResidual.txt");
	mylib::Coordinate* coord=NULL;
	mylib::Dimn_Type* coordPtr=NULL;
	mylib::Dimn_Type* auxF=new mylib::Dimn_Type[ndims];
	for(int numP=0;numP<numPartitions;numP++,numPpos+=3)
	{
		//calculate centroid
		memset(auxF,0,sizeof(mylib::Dimn_Type)*ndims);
		regionP=regionPvec[numP];
		for (int k = 0; k < regionP->rastlen; k+=2)
		{
			coord=mylib::Idx2Coord(regionP->raster[k]);
			coordPtr=(mylib::Dimn_Type*)(coord->data);
			for (mylib::Indx_Type p = regionP->raster[k]; p <= regionP->raster[k+1]; p++)//perform computation on voxel p
			{
				for(int ii=0;ii<ndims;ii++) auxF[ii]+=coordPtr[ii];

				coordPtr[0]++;//to avoid Idx2Coord all the time
			}
			mylib::Free_Array(coord);
		}
		float auxV=(float)(mylib::Region_Volume(regionP));
		if(auxV<0.5)//empty region
		{
			auxV=0;
			for(int ii=0;ii<ndims;ii++)
			{
				auxF[ii]=-1.0e32;
				outResid<<auxF[ii]<<" ";
			}
		}else{
			for(int ii=0;ii<ndims;ii++)
			{
				auxF[ii]/=auxV;
				outResid<<auxF[ii]<<" ";
			}
		}
		
		//write out row
		outResid<<auxV<<" "<<numNeighbors[numP]<<" "<<fDataVec[numP]<<" "<<fSmoothVec[numP]<<endl;;
	}

	outResid.close();
	--------------------------------------------------end debug---------------------------------------
	*/

	//release memory
	delete[] fDataVec;
	delete[] fSmoothVec;
	//delete[] auxF;
	delete[] numNeighbors;
	if(glob_param->regionPvec==NULL)
	{
		for(int numP=0;numP<numPartitions;numP++) mylib::Free_Region(regionPvec[numP]);
		free(regionPvec);
	}

	for(int ii=0;ii<ndims;ii++) mylib::Free_Array(imGrad[ii]);
	free(imGrad);


	return 0;
}













