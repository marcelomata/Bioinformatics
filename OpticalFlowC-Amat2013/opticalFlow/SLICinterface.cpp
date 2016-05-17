/*
 *  mainTest.cpp
 *  
 *
 *  Created by Amat, Fernando on 12/27/11.
 *  Copyright 2011 __MyCompanyName__. All rights reserved.
 *
 */



#include <iostream>
#include <fstream>
#include <map>
#include "math.h"
#include "SLIC.h"
#include "SLICinterface.h"
#include "opticalFlow.h"
#include "../external/Nathan/tictoc.h"

namespace mylib{
	#include "../mylib/region.h"
	#include "../mylib/connectivity.h"
	#include "../mylib/filters.h"
	#include "../mylib/water.shed.h"
};

using namespace std;


int mainTestOpticalFlow( int argc, const char* argv[] )
{


	string filenameInTarget,filenameInSource,filenameInTargetMask,filenameOut;
	mylib::uint8 maskThr=1;
	float maxDistancePartitionNeigh=80.0;

if(argc<=1)//preset values for testing. The only parameter that can be modified is maxDistancePartitionNeigh
{
	 //-----------------syntehtic images--------------------------
	
	//version 1
	/*
	mylib::uint8 maskThr=1;
	string filenameInTarget("C:/Users/Fernando/TrackingNuclei/matlabCode/syntheticImages/test01");
	string filenameInSource("C:/Users/Fernando/TrackingNuclei/matlabCode/syntheticImages/test00");
	string filenameInTargetMask("C:/Users/Fernando/TrackingNuclei/matlabCode/syntheticImages/test01_backgroundForeground");
	string filenameOut("C:/Users/Fernando/TrackingNuclei/matlabCode/syntheticImages/test01-flow.bin");
	float maxDistancePartitionNeigh=40.0;
	if(argc>1) maxDistancePartitionNeigh=atof(argv[1]);//to make it easier
	*/

	//version 2
	maskThr=1;
#if defined(_WIN32) || defined(_WIN64)
	filenameInTarget=string("E:/syntheticData/rawData/TM00000/test_00000");
	filenameInSource=string("E:/syntheticData/rawData/TM00001/test_00001");
	filenameInTargetMask=string("E:/syntheticData/rawData/TM00000/test_backgroundPredictionIlastik_00000");
	filenameOut=string("E:/syntheticData/temp/testOpticalFlow.bin");
#else
	filenameInTarget=string("/Users/amatf/cppUtils/opticalFlow-ECCV/syntheticData/rawData/TM00000/test_00000");
	filenameInSource=string("/Users/amatf/cppUtils/opticalFlow-ECCV/syntheticData/rawData/TM00001/test_00001");
	filenameInTargetMask=string("/Users/amatf/cppUtils/opticalFlow-ECCV/syntheticData/rawData/TM00000/test_backgroundPredictionIlastik_00000");
	filenameOut=string("/Users/amatf/cppUtils/opticalFlow-ECCV/syntheticData/temp/testOpticalFlow.bin");
#endif
	
	maxDistancePartitionNeigh=80.0;
	if(argc>1) maxDistancePartitionNeigh=atof(argv[1]);//to make it easier
	
	//----------------------------------------------------------------

	 //-------------------real data-----------------------------------
	/*
	mylib::uint8 maskThr=25;
	int frame=88;//source frame; target=frame+1
	float maxDistancePartitionNeigh=20.0;
	if(argc>1) maxDistancePartitionNeigh=atof(argv[1]);//to make it easier
	if(argc>2) frame=atoi(argv[2]);
	//int frame=atoi(argv[1]);
	char bufferTarget[128],bufferSource[128],bufferdMax[128];
	sprintf(bufferTarget,"%.5d",frame+1);
	sprintf(bufferSource,"%.5d",frame);
	sprintf(bufferdMax,"%d",((int)(maxDistancePartitionNeigh)));
	string itoaTarget(bufferTarget),itoaSource(bufferSource),itoadMax(bufferdMax);
	*/

	//full version drosophila
	/*
	string imgPrefix("E:/11-11-02/Dme_E2_HistGFP_01_20111102_173920.fused/Drosophila.Multifused.blending.TM");
	string imgSuffix("/CM0_CM1_CHN00_CHN01.fusedStack.filtered_100_topHat2DRad24_gaussianFilter50x50x10_");
	
	string filenameInTarget(imgPrefix+ itoaTarget + imgSuffix + itoaTarget);
	string filenameInSource(imgPrefix+ itoaSource + imgSuffix + itoaSource);
	string filenameInTargetMask(imgPrefix+ itoaTarget + imgSuffix + "backgroundPredictionIlastik_" + itoaTarget);
	string filenameOut(imgPrefix+ itoaSource + imgSuffix + "flowArray_dMax" + itoadMax + "_" + itoaSource +".bin");
	*/
	//full version zebrafish
	/*
	string imgPrefix("E:/OpticalFlow/07-11-26_Zebrafish_Histone-eGFP/TM_");
	string imgSuffix("/stack_");
	
	string filenameInTarget(imgPrefix+ itoaTarget + imgSuffix + itoaTarget);
	string filenameInSource(imgPrefix+ itoaSource + imgSuffix + itoaSource);
	string filenameInTargetMask(imgPrefix+ itoaTarget + imgSuffix + "backgroundPredictionIlastik_" + itoaTarget);
	string filenameOut(imgPrefix+ itoaTarget + imgSuffix + "flowArray_dMax" + itoadMax + "_" + itoaTarget +".bin");
	*/

	//croppped version drosophila
	/*
	string imgPrefix("E:/11-11-02/temp/opticalFlow2");//frame=88 for test2; frame=90 for test1
	string imgSuffix("/CM0_CM1_CHN00_CHN01.fusedStack.filtered_100_topHat2DRad24_gaussianFilter50x50x10_");

	string filenameInTarget(imgPrefix + imgSuffix + itoaTarget);
	string filenameInSource(imgPrefix + imgSuffix + itoaSource);
	string filenameInTargetMask(imgPrefix + imgSuffix + "backgroundPredictionIlastik_" + itoaTarget);
	string filenameOut(imgPrefix+ imgSuffix + "flowArray_dMax"+ itoadMax  +"_"+ itoaSource +".bin");
	*/


	//ssTEM 2D data
	/*
	string imgPrefix("E:/ssTEM-TrackEM2-data/180-220-int");
	string imgSuffix("/180-220-int-UINT16-");

	string filenameInTarget(imgPrefix + imgSuffix + itoaTarget);
	string filenameInSource(imgPrefix + imgSuffix + itoaSource);
	string filenameInTargetMask(imgPrefix + imgSuffix + "backgroundPredictionIlastik_" + itoaTarget);
	string filenameOut(imgPrefix+ imgSuffix + "flowArray_dMax"+ itoadMax  +"_"+ itoaSource +".bin");
	*/
	
	//FIB 2D data
	/*
	string imgPrefix("E:/FIB_AntennaLobe_6nm_Viren_HEss");
	string imgSuffix("/fib_aligned.UINT16-");

	string filenameInTarget(imgPrefix + imgSuffix + itoaTarget);
	string filenameInSource(imgPrefix + imgSuffix + itoaSource);
	string filenameInTargetMask(imgPrefix + imgSuffix + "backgroundPredictionIlastik_" + itoaTarget);
	string filenameOut(imgPrefix+ imgSuffix + "flowArray_dMax"+ itoadMax  +"_"+ itoaSource +".bin");
	
	*/
	//------------------------------------------------------------------------------------------
}else if(argc==6)//we provide 
{
	filenameInSource=string(argv[1]);
	filenameInTarget=string(argv[2]);
	filenameInTargetMask=string(argv[3]);
	filenameOut=string(argv[4]);
	maxDistancePartitionNeigh=atof(argv[5]);
}else{
	cout<<"ERROR: wrong number of parameters to call optical flow"<<endl;
	exit(2);
}


	TicTocTimer tt=tic();


	mylib::Array* flowArray=NULL;//array is generated and allocated inside routine
	int err=opticalFlow_anisotropy5(filenameInSource,filenameInTarget,filenameInTargetMask,maskThr,flowArray,maxDistancePartitionNeigh);

	cout<<"Optical flow took "<<toc(&tt)<<" secs"<<endl;
	//-----------------------write flow array-------------------------
	
	cout<<"Writing out optical flow to file "<<filenameOut<<endl;
	ofstream outBin;
	//outBin.open(("/Users/amatf/TrackingNuclei/matlabCode/syntheticImages/flow_pyramidLevel"+itoa2+".bin").c_str(),ios::binary | ios::out);
	outBin.open(filenameOut.c_str(),ios::binary | ios::out);
	
	if(outBin.is_open()==false)
	{
		cout<<"ERROR: at mainTestOpticalFlow: flow array file "<<filenameOut<<" could not be written"<<endl;
		return 3;
	}
	outBin.write((char*)(flowArray->data),sizeof(mylib::float32)*(flowArray->size));
	outBin.close();
	
	//-----------------------------------------------------

	mylib::Free_Array(flowArray);
	return err;
}

//---------------------------------------------------------------------------
int opticalFlow_anisotropy5(const string& filenameInSource,const  string& filenameInTarget,const string& filenameInTargetMask,mylib::uint8 maskThr,mylib::Array*& imgFlow,float maxDistancePartitionNeigh)
{
	string sufixGaussianPyramid("GaussianPyramidLevel");//TODO: allow to compute pyramid on the fly if needed instead of reading it from disk

	int STEP=5;//STEP in Z is modified according to anisotropy. Variable to use in SLIC superpixel generation
	double m=10.0;
	//float maxDistancePartitionNeigh=20.0;//maximum distance to consider two regions neighbors and add them to teh smoothing energy term. It is appropriately scaled for each pyramid level
	int superPixelMode=0;//0->SLIC+foreground mask;1->foreground mask+SLIC+foreground mask;2->super-pixel=single-pixel;3->super-pixel=square grid STEPxSTEPxSTEPZ + foreground mask
	const int numLevelsPyramid=3;
	float scale[numLevelsPyramid][3];
	bool smoothOn=true;//small smoothing of zero level pyramid to faicilitate gradient descend

	for(int ii=0;ii<numLevelsPyramid;ii++)
		for(int jj=0;jj<3;jj++) scale[ii][jj]=1.0f;

	scale[0][2]=5.0f;//5.0f
	if(numLevelsPyramid>1) scale[1][2]=2.5f;//2.5f

	globs_LBFGS_ lbfgs_param;
	lbfgs_param.lambda=800.0;//automatically adjusted for different pyramid levels
	lbfgs_param.deltaHdataTerm=40;//this represents intensity different in the image
	lbfgs_param.deltaHsmoothTerm=3;//this represents flow difference in the image. Use delta<0 for L1 norm;use delta~=Inf for L2 norm

	lbfgs_param.verbose=00;//debugging info. Set to zero for none

	ifstream inParam("E:/11-11-02/temp/opticalFlow/searchGridParam.txt");
	float aux=maxDistancePartitionNeigh;
	if(inParam.is_open())
	{
		cout<<"DEBUGGING: opticalFlow_anisotropy5: reading parameters from txt file"<<endl;
		inParam>>lbfgs_param.lambda;
		inParam>>lbfgs_param.deltaHdataTerm;
		inParam>>lbfgs_param.deltaHsmoothTerm;
		inParam>>maxDistancePartitionNeigh;
		if(fabs(aux-maxDistancePartitionNeigh)>1e-3)
		{
			cout<<"ERROR: maxDistance has to be changed from command line"<<endl;
			exit(3);
		}
		inParam.close();
	}

	

	//-----------------------------------------------------------------------

	//read pyramid for target
	mylib::Array** imgTargetPyramid=(mylib::Array**)malloc(sizeof(mylib::Array*)*numLevelsPyramid);
	string filenameAux;
	string itoa;
	char buffer[64];
	for(int ii=0;ii<numLevelsPyramid;ii++)
	{
		sprintf(buffer,"%d",ii);
		itoa=string(buffer);
		if(ii==0)
		{
			filenameAux=filenameInTarget+".tif";
		}else{
			filenameAux=filenameInTarget+"_"+sufixGaussianPyramid+ itoa + ".tif";
		}
		imgTargetPyramid[ii]=mylib::Read_Image((char*)filenameAux.c_str(),0);
		if(imgTargetPyramid[ii]==NULL)
		{
			cout<<"ERROR: opening imgTarget pyramid "<<filenameAux<<endl;
			return (2);
		}
		if(ii==0 & smoothOn==true)//smooth bottom of the pyramid: tends to wotk better for gradient descend
		{
			double sigma=1.5;
			for (int i = 0; i < imgTargetPyramid[ii]->ndims; i++)
			{
				double sigmaAux=sigma/scale[ii][i];
				sigmaAux=max(0.5,sigmaAux);
				mylib::Filter_Dimension(imgTargetPyramid[ii],mylib::Gaussian_Filter(sigmaAux,(int)(ceil(5.0*sigmaAux))),i);
			}
		}
	}
	//read mask for target
	mylib::Array *imgMask=mylib::Read_Image((char*)(filenameInTargetMask+".tif").c_str(),0);
	if(imgMask==NULL)
	{
		cout<<"ERROR: opening imgTargetMask "<<filenameInTargetMask+".tif"<<endl;
		exit(2);
	}
	if(imgMask->type!=mylib::UINT8_TYPE)
	{
		cout<<"ERROR: in type of imgTargetMask "<<filenameInTargetMask<<endl;
		exit(2);
	}

	//generate supervoxels using SLIC code
	int **klabels=NULL;
	int numlabels=0;
	int err=0;
	double anisotropyZ=scale[0][2];
	int ndims=imgTargetPyramid[0]->ndims;
	
	cout<<"Super-pixel mode="<<superPixelMode<<endl;
	switch(superPixelMode)
	{
	case 0:
		if(ndims>2)
			err=SLICsupervoxels(imgTargetPyramid[0], klabels,numlabels, STEP, m,anisotropyZ);
		else
			err=SLICsuperpixels(imgTargetPyramid[0], klabels,numlabels, STEP, m);
		break;
	case 1:
		if(ndims>2)
			err=SLICsupervoxels(imgTargetPyramid[0], klabels,numlabels, STEP, m,anisotropyZ,imgMask,maskThr);
		else
			err=SLICsuperpixels(imgTargetPyramid[0], klabels,numlabels, STEP, m,imgMask,maskThr);
		break;
	case 2:
	//================================================================================
	//----------------testing: no superpixels--------------------------------------
	{
		int depth;
		if(imgTargetPyramid[0]->ndims<3)
			depth=1;
		else
			depth = imgTargetPyramid[0]->dims[2];

		int width = imgTargetPyramid[0]->dims[0],height = imgTargetPyramid[0]->dims[1];
	
		//--------------------------------------------------
		//allocate memory if it has not been previously allocated
		int sz = width*height;
		long int countP=-1;//kalebels uses -1 as no-l;abel value
		if(klabels==NULL)
		{
			klabels = new int*[depth];
			for( int d = 0; d < depth; d++ )
			{
				klabels[d] = new int[sz];
				for( int s = 0; s < sz; s++ )
				{
					countP++;
					klabels[d][s] = countP;
				}
			}
		}
		numlabels=countP;
	}
	//================================================================================
	break;

	case 3:
		{
			int depth;
		if(imgTargetPyramid[0]->ndims<3)
			depth=1;
		else
			depth = imgTargetPyramid[0]->dims[2];

		int width = imgTargetPyramid[0]->dims[0],height = imgTargetPyramid[0]->dims[1];
		//--------------------------------------------------
		//allocate memory if it has not been previously allocated
		int sz = width*height;
		//long int countP=0;
		if(klabels==NULL)
		{
			klabels = new int*[depth];
			for( int d = 0; d < depth; d++ )
			{
				klabels[d] = new int[sz];
				for( int s = 0; s < sz; s++ )//preinitialize with -1
				{
					klabels[d][s] = -1;
				}
			}
		}
		//generate square "super-pixels"
		int STEPZ=min(STEP,max(3,(int)ceil(double(STEP)/anisotropyZ)));

		numlabels=0;
		//long int pos=0;
		int x,y,z;
		for(z=0;z<depth;z+=STEPZ)
		{
			for(y=0;y<height;y+=STEP)
			{
				for(x=0;x<width;x+=STEP)
				{
					//paint rectangular region with label value
					for(int dz=z;dz<z+STEPZ && dz<depth;dz++)
						for(int dy=y;dy<y+STEP && dy<height;dy++)
							for(int dx=x;dx<x+STEP && dx<width;dx++)
								klabels[dz][dx+width*dy]=numlabels;

					numlabels++;
				}
			}
		}

		break;
		}
	default:
		cout<<"ERROR: at mainOpticalFlow "<<endl;
		err=1;
	}


	if(err>0) return err; 


	//save results in a binary .dat file
	//string filenameOutSupervoxels("test.txt");
	//string pathOut("/Users/amatf/TrackingNuclei/matlabCode/syntheticImages/");
	//SLIC::SaveSupervoxelLabels(klabels,imgTargetPyramid[0]->dims[0],imgTargetPyramid[0]->dims[1],imgTargetPyramid[0]->dims[2],filenameOutSupervoxels,pathOut);
	cout<<"STEP="<<STEP<<";Total number of super-pixels="<<numlabels<<";numVoxels="<<imgTargetPyramid[0]->size<<endl;
	

	//parse SLIC results (klabels) into Mylib::array format and mask background pixels
	mylib::Array *imgLabels=mylib::Make_Array(mylib::PLAIN_KIND, mylib::UINT32_TYPE, imgTargetPyramid[0]->ndims, imgTargetPyramid[0]->dims);//label arrays have to be unsigned in Mylib
	err=parseSLICVoxelLabelsToMylibArray(klabels,numlabels,imgLabels,imgMask,maskThr);
	if(err>0) return err;

	//release klabels
	int depth;
	if(imgTargetPyramid[0]->ndims<3)
		depth=1;
	else
		depth = imgTargetPyramid[0]->dims[2];


	if(klabels!=NULL)
	{
		for( int d = 0; d < depth; d++ )
			delete []klabels[d];
		delete[] klabels;
		klabels=NULL;
	}
	cout<<"After applying background/foreground mask: Total number of super-pixels="<<numlabels<<";numVoxels="<<imgTargetPyramid[0]->size<<endl;


	//------------------------------------------------
	/*
	cout<<"DEBUGGING: writing super-pixel after foreground background"<<endl;
	//mylib::Write_Image("C:/Users/Fernando/TrackingNuclei/matlabCode/syntheticImages/imgLabels_superVoxel.tif",imgLabels,mylib::DONT_PRESS);
	ofstream outSP("E:/11-11-02/temp/opticalFlow2/imgLabels_superVoxel.bin",ios::binary | ios::out);
	if(outSP.is_open()==false)
		cout<<"ERROR: opening file to write super pixels"<<endl;
	outSP.write((char*)(imgLabels->data),sizeof(mylib::uint32)*imgLabels->size);
	outSP.close();
	exit(2);
	*/
	//------------------------------------------------

	//build pyramid for image label
	mylib::Array** imgLabelPyramid=NULL;
	int* numlabelsPyramid=new int[numLevelsPyramid];
	if(anisotropyZ>=4 && anisotropyZ<=6)
		err=buildPyramidImageLabel_anisotropy5(imgLabels, imgLabelPyramid,numlabels, numlabelsPyramid,numLevelsPyramid);
	else{
		cout<<"ERROR: pyramid code not ready for isotropic images"<<endl;
		exit(2);
	}
	if(err>0) return err;


	//load source pyramid images for optical flow
	mylib::Array** imgSourcePyramid=(mylib::Array**)malloc(sizeof(mylib::Array*)*numLevelsPyramid);
	for(int ii=0;ii<numLevelsPyramid;ii++)
	{
		sprintf(buffer,"%d",ii);
		itoa=string(buffer);
		if(ii==0)
		{
			filenameAux=filenameInSource+".tif";
		}else{
			filenameAux=filenameInSource+"_"+sufixGaussianPyramid+ itoa + ".tif";
		}
		imgSourcePyramid[ii]=mylib::Read_Image((char*)filenameAux.c_str(),0);
		if(imgSourcePyramid[ii]==NULL)
		{
			cout<<"ERROR: opening imgSource pyramid "<<filenameAux<<endl;
			return (2);
		}

		if(ii==0 & smoothOn==true)//smooth bottom of the pyramid: tends to wotk better for gradient descend
		{
			double sigma=1.5;
			for (int i = 0; i < imgSourcePyramid[ii]->ndims; i++)
			{
				double sigmaAux=sigma/scale[ii][i];
				sigmaAux=max(0.5,sigmaAux);
				mylib::Filter_Dimension(imgSourcePyramid[ii],mylib::Gaussian_Filter(sigmaAux,(int)(ceil(5.0*sigmaAux))),i);
			}
		}
	}

	//TODO: break the code here so I can get pyramid levels for images and label fields. This should be useful for other application aside from opticalFlow

	//calculate flow across different levels in the pyramid
	mylib::Partition *imgLabelPartition;
	vector<flow3D> flow;
	mylib::Array* flowArray=NULL;//stores flow for each voxel so we can go from regions to voxels
	vector<vector<pair<int,double> > > partitionNeigh;
	const double lambda_L0=lbfgs_param.lambda;
	for(int ii=numLevelsPyramid-1;ii>=0;ii--)
	{
		cout<<"Computing flow at level "<<ii<<" of the pyramid with dims ";
		for(int aa=0;aa<ndims;aa++) cout<<imgTargetPyramid[ii]->dims[aa]<<"x";
		cout<<endl;

		imgLabelPartition=generateMylibPartition(numlabelsPyramid[ii],imgTargetPyramid[ii],imgLabelPyramid[ii]);
		cout<<"Num vertex in partition="<<mylib::Get_Partition_Vertex_Count(imgLabelPartition)<<" can be colored with "<<mylib::Get_Partition_Color_Count(imgLabelPartition)<<" colors"<<endl;

		//calculate near neighbors graphs
		partitionNeighbors(imgLabelPartition, maxDistancePartitionNeigh/pow(2.0,ii),scale[ii], partitionNeigh,lbfgs_param.maxVolume);
		lbfgs_param.partitionNeigh=&partitionNeigh;


		//--------------------------------------debug: write out flow based on superpixels-----------------------------------------
		/*
		{
		char bufferF[128];
		sprintf(bufferF,"%.5d",ii);
		string itoa(bufferF);
		ofstream outF(("C:/Users/Fernando/TrackingNuclei/matlabCode/syntheticImages/flowBefore_" + itoa + ".txt").c_str());
		for(unsigned int aa=0;aa<flow.size();aa++) outF<<flow[aa].x<<" "<<flow[aa].y<<" "<<flow[aa].z<<endl;
		outF.close();
		}
		*/
		
		//------------------------------------------------------------------------------------

		//resize flow and update from previous iteration
		err=getFlowRegionsFromFlowArray_anisotropy5(flowArray,imgLabelPartition,ii,flow,partitionNeigh);
		if(err>0) exit(err);
		//--------------------------------------debug: write out flow based on superpixels-----------------------------------------
		/*
		{
		char bufferF[128];
		sprintf(bufferF,"%.5d",ii);
		string itoa(bufferF);
		ofstream outF(("C:/Users/Fernando/TrackingNuclei/matlabCode/syntheticImages/flowAfter_" + itoa + ".txt").c_str());
		for(unsigned int aa=0;aa<flow.size();aa++) outF<<flow[aa].x<<" "<<flow[aa].y<<" "<<flow[aa].z<<endl;
		outF.close();
		}
		*/
		//------------------------------------------------------------------------------------

		//perform optical flow
		lbfgs_param.lambda=lambda_L0*((double)numlabelsPyramid[ii])/((double)numlabelsPyramid[0]);//adjust lambda
		lbfgs_param.pyramidLevel=ii;
		err=calculateOpticalFlow(imgSourcePyramid[ii],imgTargetPyramid[ii],imgLabelPartition,flow,scale[ii],lbfgs_param);
		if(err>0) exit(err);

		//--------------------------------------debug: write out flow based on superpixels-----------------------------------------
		//writeFlowFromPartition(std::cout,imgLabelPartition,flow);
		//------------------------------------------------------------------------------------

		//transforms regions into a flow array
		if(flowArray!=NULL) 
		{
			mylib::Free_Array(flowArray);
			flowArray=NULL;
		}
		flowArray=getFlowArrayFromRegions(imgLabelPartition,flow);

		//-----------------------debug: write flow array-------------------------
		/*
		cout<<"DEBUGGING: writing out flowArray. File is "<<flowArray->ndims<<"-D float32 of size "<<flowArray->dims[0]<<"x"<<flowArray->dims[1]<<"x"<<flowArray->dims[2]<<"x"<<flowArray->dims[3]<<endl;
		ofstream outBin;
		char buffer2[128];
		sprintf(buffer2,"%d",ii);
		string itoa2=string(buffer2);
		outBin.open(("C:/Users/Fernando/TrackingNuclei/matlabCode/syntheticImages/flow_pyramidLevel"+itoa2+".bin").c_str(),ios::binary | ios::out);
		if(!outBin.is_open())
		{
			cout<<"ERROR: file to save flowArray could not be opened"<<endl;
		}
		outBin.write((char*)(flowArray->data),sizeof(mylib::float32)*(flowArray->size));
		outBin.close();
		*/
		/*
		if(ii==0)
		{
			cout<<"DEBUGGING: drawing canvas for partition"<<endl;
			mylib::Array* canvas=mylib::Make_Array(mylib::RGB_KIND,mylib::UINT8_TYPE,imgSourcePyramid[ii]->ndims,imgSourcePyramid[ii]->dims);
			memset(canvas->data,0,canvas->size);
			canvas=mylib::Draw_Partition(canvas,imgLabelPartition,1.0);
			mylib::Write_Image("E:/11-11-02/temp/opticalFlow2/imgLabels_superVoxel.tif",canvas,mylib::DONT_PRESS);
			exit(2);
		}
		*/
		//-----------------------------------------------------


		//------------------------debug: analyze energy residuals in detail-------------------
		
		if(ii==0)
		{
			cout<<"DEBUGGING: analyzing energy residuals in detail for level "<<ii<<endl;

			//---------------------substitute flow array with ground truth------------------
			/*
			string flowGT("E:/11-11-02/temp/opticalFlow2/CM0_CM1_CHN00_CHN01.fusedStack.filtered_100_topHat2DRad24_gaussianFilter50x50x10_flowArrayGT_00088.bin");
			ifstream flowArrayGT(flowGT.c_str(),ios::binary|ios::in);
			if(flowArrayGT.is_open()==false)
			{
				cout<<"ERROR: ground truth file "<<flowGT<<" could not be opened"<<endl;
				exit(3);
			}else{
				cout<<"Using file "<<flowGT<<" as groundtruth to calculate energy"<<endl;
			}
			float *flowArrayAux=new float[flowArray->size];
			flowArrayGT.read((char*)flowArrayAux,flowArray->size);
			if(flowArrayGT.fail())
			{
				cout<<"ERROR: reading binary file. Sizes do not seem to match"<<endl;
			}

			flowArrayGT.close();

			for(int ii=0;ii<flowArray->size;ii++)//ground truth might not be complete->we "substitute" missing parts with current solution
			{
				if((flowArrayAux[ii]!=flowArrayAux[ii])==false)
					((mylib::float32*)(flowArray->data))[ii]=flowArrayAux[ii];
			}
			delete[] flowArrayAux; 
			*/
			//------------------------------------------------------------------------------
			calculateEnergyOpticalFlow(flowArray,&lbfgs_param);
			//exit(3);
		}
		
		//-------------------------------------------------------------------------------------


		mylib::Free_Partition(imgLabelPartition);//this also frees the associate label image!!
	}

	//---------------------------------debug-------------------------------------------
	//write out the flow
	/*
	string pathOut("/Users/amatf/TrackingNuclei/matlabCode/syntheticImages/");
	string filenameOutFlow("testFlow.txt");
	ofstream outFlow((pathOut + filenameOutFlow).c_str());
	for(unsigned int ii=0;ii<flow.size();ii++)
	{
		outFlow<<flow[ii].x<<" "<<flow[ii].y<<" "<<flow[ii].z<<";"<<endl;
	}
	outFlow.close();
	*/
	//-----------------------------------------------------------------------------------

	//release memory
	for(int ii=0;ii<numLevelsPyramid;ii++)
	{
		mylib::Free_Array(imgTargetPyramid[ii]);
		mylib::Free_Array(imgSourcePyramid[ii]);
	}
	//mylib::Free_Array(imgLabels);//free_partition already does this


	free(imgLabelPyramid);
	free(imgTargetPyramid);
	free(imgSourcePyramid);
	delete[] numlabelsPyramid;
	

	//to check valgrind properly
	//mylib::Reset_Array();
	//mylib::Reset_Region();
	//mylib::Reset_Partition();

	if(imgFlow!=NULL) mylib::Free_Array(imgFlow);
	imgFlow=flowArray;

	return 0;
}

//===================================================================================
int SLICsupervoxels(mylib::Array *img,int**& klabels,int &numlabels,int STEP,int m,double anisotropyZ,mylib::Array *imgBckFwdMask,mylib::uint8 maskThr)
{
	if(img->ndims!=3)
	{
		cout<<"ERROR: at SLICsupervoxels: image must have 3 dimensions"<<endl;
		return 1;
	}
	
	int width = img->dims[0],height = img->dims[1], depth = img->dims[2];
	
	//--------------------------------------------------
	//allocate memory if it has not been previously allocated
	int sz = width*height;
	if(klabels==NULL)
	{
		klabels = new int*[depth];
		for( int d = 0; d < depth; d++ )
		{
			klabels[d] = new int[sz];
			for( int s = 0; s < sz; s++ )
			{
				klabels[d][s] = -1;
			}
		}
	}
	
	imageType* imgData = (imageType*)(img->data);
	numlabels = 0;
	SLIC slic;
	if(imgBckFwdMask==NULL)//user does not provide background/foreground mask
		slic.DoSupervoxelSegmentation(imgData, width, height,depth, klabels, numlabels, STEP, m, anisotropyZ);
	else//user provides foreground/background mask->we only seed superpixels in foreground
	{
		if(imgBckFwdMask->type!=mylib::UINT8_TYPE)
		{
			cout<<"ERROR: at SLICsupervoxels: foreground/background mask must be UINT8"<<endl;
			return 2;
		}
		mylib::uint8* imgBckFwdMaskPtr=(mylib::uint8*)(imgBckFwdMask->data);
		slic.DoSupervoxelSegmentationWithMask(imgData, width, height,depth, klabels, numlabels, STEP, m, anisotropyZ,imgBckFwdMaskPtr,maskThr);
	}
	
	return 0;
	 
}
//===================================================================================
int SLICsuperpixels(mylib::Array *img,int**& klabels,int &numlabels,int STEP,int m,mylib::Array *imgBckFwdMask,mylib::uint8 maskThr)
{
	if(img->ndims!=2)
	{
		cout<<"ERROR: at SLICsuperpixels: image must have 2 dimensions"<<endl;
		return 1;
	}
	
	int width = img->dims[0],height = img->dims[1];
	int depth=1;//to make code compatible with 3D
	//--------------------------------------------------
	//allocate memory if it has not been previously allocated
	int sz = width*height;
	if(klabels==NULL)
	{
		klabels = new int*[depth];//to make code compatible with 3D
		for( int d = 0; d < depth; d++ )
		{
			klabels[d] = new int[sz];
			for( int s = 0; s < sz; s++ )
			{
				klabels[d][s] = -1;
			}
		}
	}
	
	imageType* imgData = (imageType*)(img->data);
	numlabels = 0;
	SLIC slic;
	if(imgBckFwdMask==NULL)//user does not provide background/foreground mask
		slic.DoSuperpixelSegmentation_ForGivenStepSize(imgData, width, height, klabels[0], numlabels, STEP, m);
	else//user provides foreground/background mask->we only seed superpixels in foreground
	{
		if(imgBckFwdMask->type!=mylib::UINT8_TYPE)
		{
			cout<<"ERROR: at SLICsuperpixels: foreground/background mask must be UINT8"<<endl;
			return 2;
		}
		mylib::uint8* imgBckFwdMaskPtr=(mylib::uint8*)(imgBckFwdMask->data);
		slic.DoSupervoxelSegmentationWithMask(imgData, width, height,1, klabels, numlabels, STEP, m, 1.0,imgBckFwdMaskPtr,maskThr);
	}
	
	return 0;
	 
}


//========================================================================================================
mylib::Partition* parseVoxelLabelsToMylibPartition(int **klabels,int& numlabels,mylib::Array *img,mylib::Array *imgLabels)
{
	//copy klabels to imgLabels array
	mylib::Dimn_Type sz=imgLabels->dims[0]*imgLabels->dims[1];
	mylib::uint32 *imgLabelsPtr=(mylib::uint32*)(imgLabels->data);
	mylib::Dimn_Type pos=0;
	for(mylib::Dimn_Type ii=0;ii<imgLabels->dims[2];ii++)
	{
		//memcpy(&(imgLabelsPtr[pos]),klabels[ii],sizeof(int)*sz); //mylib uses unsigned int for labels and considers label=0 as background
		//pos+=sz;
		for(mylib::Dimn_Type kk=0;kk<sz;kk++)
		{
			imgLabelsPtr[pos++]=klabels[ii][kk]+1;
		}
	}

	//perform floodfill in order generate regions
	if(img->type>2)
	{
		cout<<"ERROR: parseVoxelLabelsToMylibPartition: mylib::Make_Partition does not support float image type"<<endl;
		exit(3);
	}
	if(imgLabels->type!=mylib::UINT32_TYPE)
	{
		cout<<"ERROR: parseVoxelLabelsToMylibPartition: mylib::Make_Partition needs UINT32_TYPE for imgLabels"<<endl;
		exit(4);
	}
	mylib::Partition *imgLabelPartition=mylib::Make_Partition(img, imgLabels, numlabels, 0, 1);//(3^n-1)-connectivity and label recoloring to convert to UINT8

	if(mylib::Get_Partition_Color_Count(imgLabelPartition)<256)
		imgLabels=mylib::Convert_Array_Inplace (imgLabels, imgLabels->kind, mylib::UINT8_TYPE,8,0);//convert to UINT8 to reduce memory
	else{
		cout<<"ERROR: parseVoxelLabelsToMylibPartition: greedy coloring can not be achieved with less than 256 colors so we can not use UINT8"<<endl;
		exit(2);
	}

	//------------------------------debug---------------------------------------------------

	//to print out partition
	string auxFile("/Users/amatf/TrackingNuclei/matlabCode/syntheticImages/testLabelsUINT8.tif");
	cout<<"DEBUG: parseVoxelLabelsToMylibPartition: writing labels at "<< auxFile<<endl;
	mylib::Write_Image((char*)(auxFile.c_str()),imgLabels,mylib::DONT_PRESS);

	//mylib::Array *imgRGB=mylib::Convert_Array (img, mylib::RGB_KIND, mylib::UINT8_TYPE,8,0);
	//imgRGB=mylib::Draw_Partition(imgRGB,auxP,0.4);
	//mylib::Write_Image((char*)(auxFile.c_str()),imgRGB,mylib::DONT_PRESS);
	//mylib::Free_Array(imgRGB);
	//--------------------------------------------------------------------------
	return imgLabelPartition;
}


//========================================================================================================
mylib::Partition* generateMylibPartition(int& numlabels,mylib::Array *img,mylib::Array *imgLabels)
{
	//perform floodfill in order generate regions
	if(img->type>2)
	{
		cout<<"ERROR: parseVoxelLabelsToMylibPartition: mylib::Make_Partition does not support float image type"<<endl;
		exit(3);
	}
	if(imgLabels->type!=mylib::UINT32_TYPE)
	{
		cout<<"ERROR: parseVoxelLabelsToMylibPartition: mylib::Make_Partition needs UINT32_TYPE for imgLabels"<<endl;
		exit(4);
	}
	mylib::Partition *imgLabelPartition=mylib::Make_Partition(img, imgLabels, numlabels, 0, 1);//(3^n-1)-connectivity and label recoloring to convert to UINT8

	if(mylib::Get_Partition_Color_Count(imgLabelPartition)<256)
		imgLabels=mylib::Convert_Array_Inplace (imgLabels, imgLabels->kind, mylib::UINT8_TYPE,8,0);//convert to UINT8 to reduce memory
	else{
		cout<<"ERROR: parseVoxelLabelsToMylibPartition: greedy coloring can not be achieved with less than 256 colors so we can not use UINT8"<<endl;
		exit(2);
	}

	//------------------------------debug---------------------------------------------------
	/*
	//to print out partition
	string auxFile("/Users/amatf/TrackingNuclei/matlabCode/syntheticImages/testLabelsUINT8.tif");
	cout<<"DEBUG: parseVoxelLabelsToMylibPartition: writing labels at "<< auxFile<<endl;
	mylib::Write_Image((char*)(auxFile.c_str()),imgLabels,mylib::DONT_PRESS);

	//mylib::Array *imgRGB=mylib::Convert_Array (img, mylib::RGB_KIND, mylib::UINT8_TYPE,8,0);
	//imgRGB=mylib::Draw_Partition(imgRGB,auxP,0.4);
	//mylib::Write_Image((char*)(auxFile.c_str()),imgRGB,mylib::DONT_PRESS);
	//mylib::Free_Array(imgRGB);
	 */
	//--------------------------------------------------------------------------
	return imgLabelPartition;
}


//========================================================================================================
mylib::Partition* parseVoxelLabelsToMylibPartition(int **klabels,int& numlabels,mylib::Array *img,mylib::Array *imgLabels,mylib::Array *imgBckFwdMask,mylib::uint8 maskThr,int STEP)
{
	//copy klabels to imgLabels array
	mylib::Dimn_Type sz=imgLabels->dims[0]*imgLabels->dims[1];
	mylib::uint8* imgBckFwdMaskPtr=(mylib::uint8*)(imgBckFwdMask->data);
	mylib::Dimn_Type pos=0;


	//blackout voxels that are background
	unsigned char* mapIdBool=new unsigned char[numlabels];//boolean indicator weather label is used in the foreground
	memset(mapIdBool,0,sizeof(unsigned char)*numlabels);
	int *sliceAux;
	for(mylib::Dimn_Type ii=0;ii<imgLabels->dims[2];ii++)
	{
		sliceAux=klabels[ii];
		for(mylib::Dimn_Type kk=0;kk<sz;kk++)
		{
			if(imgBckFwdMaskPtr[pos]<maskThr)//background pixel
			{
				sliceAux[kk]=-1;
			}else if(sliceAux[kk]>=0){//foreground pixel: insert the label into the map id
				mapIdBool[sliceAux[kk]]=1;
			}
			pos++;
		}
	}

	map<int,int>  mapId;
	int countL=0;
	for(int ii=0;ii<numlabels;ii++)
	{
		if(mapIdBool[ii]==1)
		{
			mapId[ii]=countL;
			countL++;
		}
	}
	delete[] mapIdBool;


	//remap klabels
	numlabels=mapId.size();
	for(mylib::Dimn_Type ii=0;ii<imgLabels->dims[2];ii++)
	{
		sliceAux=klabels[ii];
		for(mylib::Dimn_Type kk=0;kk<sz;kk++)
		{
			if(sliceAux[kk]>=0){//foreground pixel: insert the label into the map id
				sliceAux[kk]=mapId[sliceAux[kk]];
			}
			pos++;
		}
	}

	/*
	//enforce region connectivity TODO: make sure that after applying the foreground/background mask we have not broken the connectivity of any superpixel (it should be a rare event)
	int width=img->dims[0],height=img->dims[1],depth=img->dims[2];
	SLIC::EnforceLabelConnectivity_supervoxels(width,height,depth,klabels,numlabels,STEP);
	*/

	//call original routine
	return parseVoxelLabelsToMylibPartition(klabels,numlabels,img,imgLabels);
}

//========================================================================================================
int parseSLICVoxelLabelsToMylibArray(int **klabels,int& numlabels,mylib::Array *imgLabels,mylib::Array *imgBckFwdMask,mylib::uint8 maskThr)
{
	//copy klabels to imgLabels array
	mylib::Dimn_Type sz=imgLabels->dims[0]*imgLabels->dims[1];
	mylib::Indx_Type pos=0;

	if(imgBckFwdMask->type!=mylib::UINT8_TYPE)//TODO: allow for imgBckFwdMask to be NULL (all the pixels are foreground and we do not need remapping)
	{
		cout<<"ERROR: parseVoxelLabelsToMylibPartition: background/forward mask has to be UINT8"<<endl;
		return 1;
	}
	mylib::uint8* imgBckFwdMaskPtr=(mylib::uint8*)(imgBckFwdMask->data);

	if(imgLabels->type!=mylib::UINT32_TYPE)
	{
		cout<<"ERROR: parseVoxelLabelsToMylibPartition: image labels array has to be UINT32"<<endl;
		return 1;
	}
	mylib::uint32* imgLabelsPtr=(mylib::uint32*)(imgLabels->data);

	int depth;
	if(imgLabels->ndims<3)
		depth=1;
	else
		depth = imgLabels->dims[2];


	//blackout voxels that are background
	unsigned char* mapIdBool=new unsigned char[numlabels];//boolean indicator weather label is used in the foreground
	memset(mapIdBool,0,sizeof(unsigned char)*numlabels);
	int *sliceAux;
	for(mylib::Dimn_Type ii=0;ii<depth;ii++)
	{
		sliceAux=klabels[ii];
		for(mylib::Dimn_Type kk=0;kk<sz;kk++)
		{
			if(imgBckFwdMaskPtr[pos]<maskThr)//background pixel
			{
				sliceAux[kk]=-1;
			}else if(sliceAux[kk]>=0){//foreground pixel: insert the label into the map id
				mapIdBool[sliceAux[kk]]=1;
			}
			pos++;
		}
	}

	map<int,int>  mapId;
	int countL=1;//we consider label zero as background
	for(int ii=0;ii<numlabels;ii++)
	{
		if(mapIdBool[ii]==1)
		{
			mapId[ii]=countL;
			countL++;
		}
	}
	delete[] mapIdBool;


	//remap klabels into imgLabel array
	numlabels=mapId.size();
	pos=0;
	memset(imgLabelsPtr,0,sizeof(mylib::uint32)*imgLabels->size);//reset all the array as background before remapping
	for(mylib::Dimn_Type ii=0;ii<depth;ii++)
	{
		sliceAux=klabels[ii];
		for(mylib::Dimn_Type kk=0;kk<sz;kk++)
		{
			if(sliceAux[kk]>=0){//foreground pixel: insert the label into the map id
				imgLabelsPtr[pos]=mapId[sliceAux[kk]];
			}
			pos++;
		}
	}


	//---------------------------debug: write out labels as UINT32-------------------------------
	/*
	cout<<"DEBUGGING: writing UINT32 binary file"<<endl;
	ofstream outBin;
	mylib::uint32 auxI;
	outBin.open("/Users/amatf/TrackingNuclei/matlabCode/syntheticImages/testLabelsUINT32.bin",ios::binary | ios::out);
	for(mylib::Size_Type ii=0;ii<imgLabels->size;ii++)
		{
			auxI=(mylib::uint32)(imgLabelsPtr[ii]);
			outBin.write((char*)(&auxI),sizeof(mylib::uint32));
		}
	outBin.close();
	exit(2);
	*/
	//---------------------------------------------------------------------------------------------


	//enforce region connectivity: make sure that after applying the foreground/background mask we have not broken the connectivity of any superpixel (it should be a rare event)
	//This step is needed to make sure that mylib::Partition works properly
	int err=enforceLabelConnectivity(numlabels,imgLabels);
	if(err>0) exit(err);

	return 0;
}

