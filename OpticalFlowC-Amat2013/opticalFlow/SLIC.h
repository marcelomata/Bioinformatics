// SLIC.h: interface for the SLIC class.
//===========================================================================
// This code implements the saliency method described in:
//
// Radhakrishna Achanta, Appu Shaji, Kevin Smith, Aurelien Lucchi, Pascal Fua, and Sabine Susstrunk,
// "SLIC Superpixels",
// EPFL Technical Report no. 149300, June 2010.
//===========================================================================
//	Copyright (c) 2010 Radhakrishna Achanta [EPFL]. All rights reserved.
//===========================================================================
// Email: firstname.lastname@epfl.ch
//////////////////////////////////////////////////////////////////////

#if !defined(_SLIC_H_INCLUDED_)
#define _SLIC_H_INCLUDED_


#include <vector>
#include <string>
#include <algorithm>
using namespace std;

//to make the code portable to Unix systems
#if !(defined(_WIN32) || defined(_WIN64))
#define _MAX_FNAME NAME_MAX
#endif
typedef unsigned short int imageType; //defines the kind of images we work with

class SLIC  
{
public:
	SLIC();
	virtual ~SLIC();
	void allocateLABvecvec();
	void clearLABvecvec();
	void allocateLABvec();
	void clearLABvec();
	//============================================================================
	// Superpixel segmentation for a given step size (superpixel size ~= step*step)
	//============================================================================
	void DoSuperpixelSegmentation_ForGivenStepSize(
		const unsigned int*			ubuff,//Each 32 bit unsigned int contains ARGB pixel values.
		const int					width,
		const int					height,
		int*						klabels,
		int&						numlabels,
		const int&					STEP,
		const double&				m);
	//============================================================================
	// amatfSuperpixel segmentation for a given step size (superpixel size ~= step*step)
	//============================================================================
	void DoSuperpixelSegmentation_ForGivenStepSize(
		const unsigned short int*			ubuff,//Each 32 bit unsigned int contains ARGB pixel values.
		const int					width,
		const int					height,
		int*						klabels,
		int&						numlabels,
		const int&					STEP,
		const double&				m);
	//============================================================================
	// Superpixel segmentation for a given number of superpixels
	//============================================================================
	void DoSuperpixelSegmentation_ForGivenK(
		const unsigned int*			ubuff,
		const int					width,
		const int					height,
		int*						klabels,
		int&						numlabels,
		const int&					K,
		const double&				m);
	//============================================================================
	// Supervoxel segmentation for a given step size (supervoxel size ~= step*step*step)
	//============================================================================
	void DoSupervoxelSegmentation(
		const unsigned int**		ubuffvec,
		const int&					width,
		const int&					height,
		const int&					depth,
		int**						klabels,
		int&						numlabels,
		const int&					STEP,
		const double&				m);
	//============================================================================
	//amatf: adaptation for our own type of images
	// Supervoxel segmentation for a given step size (supervoxel size ~= step*step*step)
	//============================================================================
	void DoSupervoxelSegmentation(
								  const imageType*		ubuffvec,
								  const int&					width,
								  const int&					height,
								  const int&					depth,
								  int**						klabels,
								  int&						numlabels,
								  const int&					STEP,
								  const double&				m,
								  const double&				anisotropyZ);
	//============================================================================
		//amatf: adaptation for our own type of images
		// Supervoxel segmentation for a given step size (supervoxel size ~= step*step*step/anisotropy)
		//Here we supply a foreground/background mask in order to seed super pixels only in foreground areas
		//WARNING: if maskThr is supplied, super-pixels are not guaranteeed to be connected
		//============================================================================
		void DoSupervoxelSegmentationWithMask(
									  const imageType*		ubuffvec,
									  const int&					width,
									  const int&					height,
									  const int&					depth,
									  int**						klabels,
									  int&						numlabels,
									  const int&					STEP,
									  const double&				m,
									  const double&				anisotropyZ,
									  const unsigned char*		imgBckFwdMaskPtr,
									  const unsigned char 		maskThr);
	//============================================================================
	// Save superpixel labels in a text file in raster scan order
	//============================================================================
	void SaveSuperpixelLabels(
		const int*&					labels,
		const int&					width,
		const int&					height,
		const string&				filename,
		const string&				path);
	//============================================================================
	// Save superpixel labels in a text file in raster scan, depth order
	//============================================================================
	static void SaveSupervoxelLabels(
		const int**&				labels,
		const int&					width,
		const int&					height,
		const int&					depth,
		const string&				filename,
		const string&				path);
	//============================================================================
		// Save superpixel labels in a text file in raster scan, depth order
		//============================================================================
		static void SaveSupervoxelLabels(
			 int**&				labels,
			 int&					width,
			 int&					height,
			 int&					depth,
			 string&				filename,
			 string&				path);
	//============================================================================
	// Function to draw boundaries around superpixels of a given 'color'.
	// Can also be used to draw boundaries around supervoxels, i.e layer by layer.
	//============================================================================
	void DrawContoursAroundSegments(
		unsigned int*				segmentedImage,
		const int*					labels,
		const int&					width,
		const int&					height,
		const unsigned int&			color );

private:
	//============================================================================
	// The main SLIC algorithm for generating superpixels
	//============================================================================
	void PerformSuperpixelSLIC(
		vector<double>&				kseedsl,
		vector<double>&				kseedsa,
		vector<double>&				kseedsb,
		vector<double>&				kseedsx,
		vector<double>&				kseedsy,
		int*						klabels,
		const int&					STEP,
		const vector<double>&		edgemag,
		const double&				m);
	//============================================================================
	// The main SLIC algorithm for generating supervoxels
	//============================================================================
	void PerformSupervoxelSLIC(
		vector<double>&				kseedsl,
		vector<double>&				kseedsa,
		vector<double>&				kseedsb,
		vector<double>&				kseedsx,
		vector<double>&				kseedsy,
		vector<double>&				kseedsz,
		int**						klabels,
		const int&					STEP,
		const double&				m,
		const double&				anisotropyZ=1.0);//amatf: to incorporate anisotropy in Z
	//============================================================================
	// Pick seeds for superpixels when step size of superpixels is given.
	//============================================================================
	void GetLABXYSeeds_ForGivenStepSize(
		vector<double>&				kseedsl,
		vector<double>&				kseedsa,
		vector<double>&				kseedsb,
		vector<double>&				kseedsx,
		vector<double>&				kseedsy,
		const int&					STEP,
		const bool&					perturbseeds,
		const vector<double>&		edgemag);
	//============================================================================
	// Pick seeds for superpixels when number of superpixels is input.
	//============================================================================
	void GetLABXYSeeds_ForGivenK(
		vector<double>&				kseedsl,
		vector<double>&				kseedsa,
		vector<double>&				kseedsb,
		vector<double>&				kseedsx,
		vector<double>&				kseedsy,
		const int&					STEP,
		const bool&					perturbseeds,
		const vector<double>&		edges);
	//============================================================================
	// Pick seeds for supervoxels
	//============================================================================
	void GetKValues_LABXYZ(
		vector<double>&				kseedsl,
		vector<double>&				kseedsa,
		vector<double>&				kseedsb,
		vector<double>&				kseedsx,
		vector<double>&				kseedsy,
		vector<double>&				kseedsz,
		const int&					STEP,
		const double&				anisotropyZ=1.0,
		const unsigned char*		imgBckFwdMaskPtr=NULL,
		const unsigned char 		maskThr=0);
	//============================================================================
	// Move the seeds to low gradient positions to avoid putting seeds at region boundaries.
	//============================================================================
	void PerturbSeeds(
		vector<double>&				kseedsl,
		vector<double>&				kseedsa,
		vector<double>&				kseedsb,
		vector<double>&				kseedsx,
		vector<double>&				kseedsy,
		const vector<double>&		edges);
	//============================================================================
	// Detect color edges, to help PerturbSeeds()
	//============================================================================
	void DetectLabEdges(
		const double*				lvec,
		const double*				avec,
		const double*				bvec,
		const int&					width,
		const int&					height,
		vector<double>&				edges);
	//============================================================================
	// xRGB to XYZ conversion; helper for RGB2LAB()
	//============================================================================
	void RGB2XYZ(
		const int&					sR,
		const int&					sG,
		const int&					sB,
		double&						X,
		double&						Y,
		double&						Z);
	//============================================================================
	// sRGB to CIELAB conversion
	//============================================================================
	void RGB2LAB(
		const int&					sR,
		const int&					sG,
		const int&					sB,
		double&						lval,
		double&						aval,
		double&						bval);
	//============================================================================
	// sRGB to CIELAB conversion for 2-D images
	//============================================================================
	void DoRGBtoLABConversion(
		const unsigned int*&		ubuff,
		double*&					lvec,
		double*&					avec,
		double*&					bvec);
	//============================================================================
	// sRGB to CIELAB conversion for 3-D volumes
	//============================================================================
	void DoRGBtoLABConversion(
		const unsigned int**&		ubuff,
		double**&					lvec,
		double**&					avec,
		double**&					bvec);

	//============================================================================
	// sRGB to CIELAB conversion for 3-D volumes saved as a single channel imageType
	//============================================================================
	void DoRGBtoLABConversion(
							  const imageType**&		ubuff,
							  double**&					lvec,
							  double**&					avec,
							  double**&					bvec);
	//============================================================================
	// sRGB to CIELAB conversion for 3-D volumes saved as a single channel imageType and stored in a single array (not double) using Mylib
	//============================================================================
	void DoRGBtoLABConversion(
							  const imageType*&		ubuff,
							  double**&					lvec,
							  double**&					avec,
							  double**&					bvec);
	//============================================================================
	// sRGB to CIELAB conversion for 3-D volumes saved as a single channel imageType and stored in a single array (not double) using Mylib
	//============================================================================
	void DoRGBtoLABConversion(
							  const imageType*&		ubuff,
							  double*&					lvec,
							  double*&					avec,
							  double*&					bvec);

	//============================================================================
	// Post-processing of SLIC segmentation, to avoid stray labels.
	//============================================================================
	void EnforceLabelConnectivity(
		const int*					labels,
		const int&					width,
		const int&					height,
		int*						nlabels,//input labels that need to be corrected to remove stray labels
		int&						numlabels,//the number of labels changes in the end if segments are removed
		const int&					K); //the number of superpixels desired by the user

	//============================================================================
	// Find next superpixel label; helper for EnforceLabelConnectivity()
	//============================================================================
	void FindNext(
		const int*					labels,
		int*						nlabels,
		const int&					height,
		const int&					width,
		const int&					h,
		const int&					w,
		const int&					lab,
		int*						xvec,
		int*						yvec,
		int&						count);


public://amatf so I can call them from outside
	//============================================================================
	// Post-processing of SLIC supervoxel segmentation, to avoid stray labels.
	//============================================================================
	static void EnforceLabelConnectivity_supervoxels(
		const int&					width,
		const int&					height,
		const int&					depth,
		int**						nlabels,//input labels that to be corrected. output is stored in here too.
		int&						numlabels,//the number of labels changes in the end if segments are removed
		const int&					STEP); //step size, it helps decide the minimum acceptable size

	//============================================================================
	// Find next supervoxel label; helper for EnforceLabelConnectivity()
	//============================================================================
	static void FindNext_supervoxels(
		int**						labels,
		int**						nlabels,
		const int&					depth,
		const int&					height,
		const int&					width,
		const int&					d,
		const int&					h,
		const int&					w,
		const int&					lab,
		int*						xvec,
		int*						yvec,
		int*						zvec,
		int&						count);

private:
	int										m_width;
	int										m_height;
	int										m_depth;

	double*									m_lvec;
	double*									m_avec;
	double*									m_bvec;

	double**								m_lvecvec;
	double**								m_avecvec;
	double**								m_bvecvec;
};

#endif // !defined(_SLIC_H_INCLUDED_)
