
public class SLICinterface {
	
	public SLICinterface() {
		
	}
	
	public void mainTestOpticalFlow(int argc, String argv[]) {
		String filenameInTarget, filenameInSource, filenameInTargetMask, filenameOut;
		byte maskThr = 1;
		float maxDistancePartitionNeigh=80.0f;
		
		//preset values for testing. The only parameter that can be modified is maxDistancePartitionNeigh
		if(argc<=1) {
			//version 2
			maskThr=1;
			filenameInTarget= "C:/syntheticData/rawData/TM00000/test_00000";
			filenameInSource= "C:/syntheticData/rawData/TM00001/test_00001";
			filenameInTargetMask="C:/syntheticData/rawData/TM00000/test_backgroundPredictionIlastik_00000";
			filenameOut="C:/syntheticData/temp/testOpticalFlow.bin";
			
			maxDistancePartitionNeigh=80.0f;
			if(argc>1) 
				maxDistancePartitionNeigh = Float.parseFloat(argv[1]);//to make it easier
		} else if(argc==6) { //we provide 
			filenameInSource = argv[1];
			filenameInTarget = argv[2];
			filenameInTargetMask = argv[3] ;
			filenameOut = argv[4];
			maxDistancePartitionNeigh = Float.parseFloat(argv[5]);
		} else {
			System.out.println("ERROR: wrong number of parameters to call optical flow");
			return;
		}

//TODO continue here
//			TicTocTimer tt=tic();
//
//
//			mylib::Array* flowArray=NULL;//array is generated and allocated inside routine
//			int err=opticalFlow_anisotropy5(filenameInSource,filenameInTarget,filenameInTargetMask,maskThr,flowArray,maxDistancePartitionNeigh);
//
//			cout<<"Optical flow took "<<toc(&tt)<<" secs"<<endl;
//			//-----------------------write flow array-------------------------
//			
//			cout<<"Writing out optical flow to file "<<filenameOut<<endl;
//			ofstream outBin;
//			//outBin.open(("/Users/amatf/TrackingNuclei/matlabCode/syntheticImages/flow_pyramidLevel"+itoa2+".bin").c_str(),ios::binary | ios::out);
//			outBin.open(filenameOut.c_str(),ios::binary | ios::out);
//			
//			if(outBin.is_open()==false)
//			{
//				cout<<"ERROR: at mainTestOpticalFlow: flow array file "<<filenameOut<<" could not be written"<<endl;
//				return 3;
//			}
//			outBin.write((char*)(flowArray->data),sizeof(mylib::float32)*(flowArray->size));
//			outBin.close();
//			
//			//-----------------------------------------------------
//
//			mylib::Free_Array(flowArray);
//			return err;
//		}
	}

}
