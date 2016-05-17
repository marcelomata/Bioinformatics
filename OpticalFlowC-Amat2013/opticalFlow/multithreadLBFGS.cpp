#include "multithreadLBFGS.h"


void multithreadLBFGS::calculate(double* gAux)
{

	const mylib::Array* imTarget=lbfgs_param.imTarget;
	const mylib::uint16* imSourcePtr=(mylib::uint16*)(lbfgs_param.imSource->data);

		f=0.0;
		double fAux;
		int ndims=imTarget->ndims;
		int numPpos=numPini*ndims;

		for(int numP=numPini;numP<numPend;numP++,numPpos+=ndims)
		{
			calculateDataTermOneRegion(regionPvec[numP],imTarget,imTargetDer,imSourcePtr,x.getcontent()+numPpos,scale,lbfgs_param.deltaHdataTerm,fAux,gAux+numPpos);
			f+=fAux;
		}
}

//============================================================
void multithreadLBFGS::minimize()
{
	//call minimization routine
	double epsg=1e-6;//relative change for gradient
	double epsf=1e-6;//relative change for cost function
	double epsx=1e-6;//relative change for x
	int maxits=300;
	int info=0;
	int N=x.gethighbound()-x.getlowbound()+1;
	/*
	int N=3*Np;//we need to concatenate flow as x=[u_1,v_1,w_1,....,u_N,v_N,w_N] (cache friendly)

	ap::real_1d_array x;
	x.setbounds(1,N);
	int count=0;
	for(unsigned int ii=0;ii<Np;ii++,count+=3)
	{
		x(count+1)=flow[ii].x;
		x(count+2)=flow[ii].y;
		x(count+3)=flow[ii].z;
	}
	*/
	{
		if(lbfgs_param.verbose==0)
			lbfgsminimize(N,6,x,funcgrad_residOpticalFlow_mt,NULL,&lbfgs_param,epsg,epsf,epsx,maxits,info);
		else
			lbfgsminimize(N,6,x,funcgrad_residOpticalFlow_mt,newiteration_residOpticalFlow_mt,&lbfgs_param,epsg,epsf,epsx,maxits,info);

	}
}

//================================================================================================
void multithreadLBFGS::calculateDataTermOneRegion(const mylib::Region* regionP,const mylib::Array* imTarget,const mylib::Array** imTargetDer,const mylib::uint16* imSourcePtr,const double* x,const float *scale,double deltaHdataTerm,double &fAux,double* gAux)
{
#ifndef INTERP_TRILINEAR 
#define INTERP_TRILINEAR //decides the kind of interpolation we want
#endif	
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




//=========================================================================
//=============================================================================================================
//trilinear interpolation for image intensity and derivatives calculated from finite differences)
int multithreadLBFGS::trilinearInterpolation(const double *x,const float* scale,const mylib::Array *img,const mylib::Array** img_der,double &f,double *df,mylib::Coordinate* c)
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

	//calculate each corner of the cube to interpolate on and its weight
	mylib::Indx_Type sz=img->dims[0]*img->dims[1];
	mylib::Indx_Type pCube[8];
	double wCubeFloor[3],wCubeCeil[3];
	pCube[0]=cc;pCube[1]=cc+1;pCube[2]=pCube[1]+img->dims[0];pCube[3]=pCube[0]+img->dims[0];
	pCube[4]=pCube[0]+sz;pCube[5]=pCube[1]+sz;pCube[6]=pCube[2]+sz;pCube[7]=pCube[3]+sz;//guaranteed to be within boundaries

	wCubeFloor[0]=x[0]-(double)(cPtr[0]);//value between 0 and 1
	wCubeFloor[1]=x[1]-(double)(cPtr[1]);//value between 0 and 1
	wCubeFloor[2]=x[2]-(double)(cPtr[2]);//value between 0 and 1

	wCubeCeil[0]=1.0-wCubeFloor[0];
	wCubeCeil[1]=1.0-wCubeFloor[1];
	wCubeCeil[2]=1.0-wCubeFloor[2];

	f=wCubeCeil[2] *( wCubeCeil[1] *(wCubeCeil[0] *imgPtr[pCube[0]]+wCubeFloor[0]*imgPtr[pCube[1]]) +
			wCubeFloor[1]*(wCubeFloor[0]*imgPtr[pCube[2]]+wCubeCeil[0] *imgPtr[pCube[3]]) )+
			wCubeFloor[2]*( wCubeCeil[1] *(wCubeCeil[0] *imgPtr[pCube[4]]+wCubeFloor[0]*imgPtr[pCube[5]]) +
					wCubeFloor[1]*(wCubeFloor[0]*imgPtr[pCube[6]]+wCubeCeil[0] *imgPtr[pCube[7]]) );

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

			df[d]=	wCubeCeil[2] *( wCubeCeil[1] *(wCubeCeil[0] *img_derPtr[pCube[0]]+wCubeFloor[0]*img_derPtr[pCube[1]]) +
					wCubeFloor[1]*(wCubeFloor[0]*img_derPtr[pCube[2]]+wCubeCeil[0] *img_derPtr[pCube[3]]) )+
					wCubeFloor[2]*( wCubeCeil[1] *(wCubeCeil[0] *img_derPtr[pCube[4]]+wCubeFloor[0]*img_derPtr[pCube[5]]) +
							wCubeFloor[1]*(wCubeFloor[0]*img_derPtr[pCube[6]]+wCubeCeil[0] *img_derPtr[pCube[7]]) );
		}
	}

	//mylib::Free_Array(c);

	return 0;
}


//=====================================================================================
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
void funcgrad_residOpticalFlow_mt (ap::real_1d_array x, double& f, ap::real_1d_array& g, void *params)
{
#define INTERP_TRILINEAR //decides the kind of interpolation we want

	globs_LBFGS_ *glob_param=(globs_LBFGS_*)params;
	mylib::Partition* imTargetPartition=glob_param->imTargetPartition;
	mylib::Array* imSource=glob_param->imSource;
	mylib::Array* imTarget=glob_param->imTarget;
	mylib::Array* imTarget_dx=glob_param->imTarget_dx;
	mylib::Array* imTarget_dy=glob_param->imTarget_dy;
	mylib::Array* imTarget_dz=glob_param->imTarget_dz;
	vector<vector<pair<int,double> > >* partitionNeigh=glob_param->partitionNeigh;
	//mylib::Region** regionPvec=glob_param->regionPvec;

	double lambda=glob_param->lambda;
	double deltaHsmoothTerm=glob_param->deltaHsmoothTerm;
	//double deltaHdataTerm=glob_param->deltaHdataTerm;
	//float* scale=glob_param->scale;

	if(imSource->type!=mylib::UINT16_TYPE || imTarget->type!=mylib::UINT16_TYPE)
	{
		cout<<"ERROR: funcgrad_residOpticalFlow: code expects UINT16 images"<<endl;
		exit(2);
	}

	//mylib::uint16* imSourcePtr=(mylib::uint16*)(imSource->data);

	//initialize residual and gradient
	int numPartitions=mylib::Get_Partition_Vertex_Count(imTargetPartition);
	f=0;
	double fSmooth=0;
	memset(g.getcontent(),0,sizeof(double)*3*numPartitions);

	int sizeX=x.gethighbound()-x.getlowbound()+1;
	if(3*numPartitions!=sizeX)
	{
		cout<<"ERROR: funcgrad_residOpticalFlow: size of unknowns does not much number of image regions"<<endl;
		exit(2);
	}


	int numPpos=0,numNeighPos=0;
	//int *listedges=NULL;
	//int nedges=0;;
	double fAux,gAux,auxU,auxV,auxW;
	//int ndims=imTarget->ndims;

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

	imTargetDer[0]=imTarget_dx;imTargetDer[1]=imTarget_dy;imTargetDer[2]=imTarget_dz;
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

	/*
	//single thread execution
	for(int numP=0;numP<numPartitions;numP++)
	{
		numPpos=numP*ndims;//for parallelization
		calculateDataTermOneRegion(regionPvec[numP],imTarget,imTargetDer,imSourcePtr,x.getcontent()+numPpos,scale,deltaHdataTerm,fAux,g.getcontent()+numPpos);
		f+=fAux;
		//for(int aa=0;aa<ndims;aa++) g(numPpos+1+aa)+=df[aa];//by passing g directly we avoid double copy
	}
	*/

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
	for(int numP=0;numP<numPartitions;numP++,numPpos+=3)
	{
		vector<pair<int,double> >* edges=&((*partitionNeigh)[numP]);
		double lambdaAux;
		for(vector<pair<int,double> >::const_iterator iter=edges->begin();iter!=edges->end();++iter)//we do not need to avoid double counting because it was already done during neighbor list creation
		{
			numNeighPos=3*(iter->first);
			lambdaAux=lambda*(iter->second);
			auxU=x(numPpos+1)-x(numNeighPos+1);
			auxV=x(numPpos+2)-x(numNeighPos+2);
			auxW=x(numPpos+3)-x(numNeighPos+3);
			HuberCostAndDer(auxU,deltaHsmoothTerm,fAux,gAux);
			fSmooth+=fAux*(iter->second);
			g(numPpos+1)+=lambdaAux*gAux;//*1.0
			g(numNeighPos+1)-=lambdaAux*gAux;

			HuberCostAndDer(auxV,deltaHsmoothTerm,fAux,gAux);
			fSmooth+=fAux*(iter->second);
			g(numPpos+2)+=lambdaAux*gAux;//*1.0
			g(numNeighPos+2)-=lambdaAux*gAux;

			HuberCostAndDer(auxW,deltaHsmoothTerm,fAux,gAux);
			fSmooth+=fAux*(iter->second);
			g(numPpos+3)+=lambdaAux*gAux;//*1.0
			g(numNeighPos+3)-=lambdaAux*gAux;
		}
	}

	//--------------------------------debug------------------------
	/*
	cout<<"DEBUGGING!!! funcgrad_residOpticalFlow"<<endl;
	//cout.precision(20);
	//cout.setf(ios::fixed,ios::floatfield);
	cout<<"fData="<<f<<";fSmooth="<<fSmooth<<";fData+lambda*fSmooth="<<f+lambda*fSmooth<<endl;

	cout<<"x=[";
	for(int ii=0;ii<3*numPartitions;ii+=3)
	{
		cout<<x(ii+1)<<" "<<x(ii+2)<<" "<<x(ii+3)<<";"<<endl;
	}
	cout<<"];"<<endl;

	cout<<"g=[";
	for(int ii=0;ii<3*numPartitions;ii+=3)
	{
		cout<<g(ii+1)<<" "<<g(ii+2)<<" "<<g(ii+3)<<";"<<endl;
	}
	cout<<"];"<<endl;
	//exit(2);
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

//===============================
//=====================================================================
void newiteration_residOpticalFlow_mt(int iter, const ap::real_1d_array& x, double f,const ap::real_1d_array& g, void *params)
{
	globs_LBFGS_ *glob_param=(globs_LBFGS_*)params;
	double normG=0.0;
	for(int ii=g.getlowbound();ii<=g.gethighbound();ii++) normG+=g(ii)*g(ii);
	normG=sqrt(normG);
	cout<<"Iter="<<iter<<";fData="<<glob_param->fData<<";fSmooth="<<glob_param->fSmooth<<";fData+lambda*fSmooth="<<f<<";RMS(g)="<<normG/((double)(g.gethighbound()-g.getlowbound()+1))<<endl;
}
