package amal.xml;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="FeatureDeclarations")
public class FeatureDeclarations {
	
	@XmlElement(name="SpotFeatures")
	private SpotFeatures spotFeatures;
	
	@XmlElement(name="EdgeFeatures")
	private EdgeFeatures edgeFeatures;
	
	@XmlElement(name="TrackFeatures")
	private TrackFeatures trackFeatures;
	
	public FeatureDeclarations(){
		spotFeatures = new SpotFeatures();
		edgeFeatures = new EdgeFeatures();
		trackFeatures = new TrackFeatures();
	}
	
	
	public void setSpotFeatures(SpotFeatures spotFeatures){
		this.spotFeatures=spotFeatures;
	}
	
	
	public void setEdgeFeatures(EdgeFeatures edgeFeatures){
		this.edgeFeatures=edgeFeatures;
	}
	
	
	public void setTrackFeatures(TrackFeatures trackFeatures){
		this.trackFeatures=trackFeatures;
	}
	
	public void run(){
		
		ArrayList<Feature> spotList = new ArrayList<Feature>();
		
		Feature quality = new Feature();
		quality.setFeature("QUALITY");
		quality.setName("Quality");
		quality.setShortName("Quality");
		quality.setDimension("QUALITY");
		quality.setIsInt(false);
		
		spotList.add(quality);
		
		Feature posX = new Feature();
		posX.setFeature("POSITION_X");
		posX.setName("X");
		posX.setShortName("X");
		posX.setDimension("POSITION");
		posX.setIsInt(false);
		
		spotList.add(posX);
		
		Feature posY = new Feature();
		posY.setFeature("POSITION_Y");
		posY.setName("Y");
		posY.setShortName("Y");
		posY.setDimension("POSITION");
		posY.setIsInt(false);
		
		spotList.add(posY);
		
		Feature posZ = new Feature();
		posZ.setFeature("POSITION_Z");
		posZ.setName("Z");
		posZ.setShortName("Z");
		posZ.setDimension("POSITION");
		posZ.setIsInt(false);
		
		spotList.add(posZ);
		
		Feature posT = new Feature();
		posT.setFeature("POSITION_T");
		posT.setName("T");
		posT.setShortName("T");
		posT.setDimension("TIME");
		posT.setIsInt(false);
		
		spotList.add(posT);
		
		Feature frame = new Feature();
		frame.setFeature("FRAME");
		frame.setName("Frame");
		frame.setShortName("Frame");
		frame.setDimension("NONE");
		frame.setIsInt(true);
		
		spotList.add(frame);
		
		Feature radius = new Feature();
		radius.setFeature("RADIUS");
		radius.setName("Radius");
		radius.setShortName("R");
		radius.setDimension("LENGTH");
		radius.setIsInt(false);
		
		spotList.add(radius);
		
		Feature visibility = new Feature();
		visibility.setFeature("VISIBILITY");
		visibility.setName("Visibility");
		visibility.setShortName("Visibility");
		visibility.setDimension("NONE");
		visibility.setIsInt(true);
		
		spotList.add(visibility);
		
		Feature col = new Feature();
		col.setFeature("MANUAL_COLOR");
		col.setName("Manual spot color");
		col.setShortName("Spot color");
		col.setDimension("NONE");
		col.setIsInt(true);
		
		spotList.add(col);
		
		Feature mean = new Feature();
		mean.setFeature("MEAN_INTENSITY");
		mean.setName("Mean intensity");
		mean.setShortName("Mean");
		mean.setDimension("INTENSITY");
		mean.setIsInt(false);
		
		spotList.add(mean);
		
		Feature median = new Feature();
		median.setFeature("MEDIAN_INTENSITY");
		median.setName("Median intensity");
		median.setShortName("Median");
		median.setDimension("INTENSITY");
		median.setIsInt(false);
		
		spotList.add(median);
		
		Feature min = new Feature();
		min.setFeature("MIN_INTENSITY");
		min.setName("Min intensity");
		min.setShortName("Min");
		min.setDimension("INTENSITY");
		min.setIsInt(false);
		
		spotList.add(min);
		
		Feature max = new Feature();
		max.setFeature("MAX_INTENSITY");
		max.setName("Max intensity");
		max.setShortName("Max");
		max.setDimension("INTENSITY");
		max.setIsInt(false);
		
		spotList.add(max);
		
		Feature tot = new Feature();
		tot.setFeature("TOTAL_INTENSITY");
		tot.setName("Total intensity");
		tot.setShortName("Total int.");
		tot.setDimension("INTENSITY");
		tot.setIsInt(false);
		
		spotList.add(tot);
		
		Feature std = new Feature();
		std.setFeature("STANDARD_DEVIATION");
		std.setName("Standard deviation");
		std.setShortName("Stdev.");
		std.setDimension("INTENSITY");
		std.setIsInt(false);
		
		spotList.add(std);
		
		Feature diam = new Feature();
		diam.setFeature("ESTIMATED_DIAMETER");
		diam.setName("Estimated diameter");
		diam.setShortName("Diam.");
		diam.setDimension("LENGTH");
		diam.setIsInt(false);
		
		spotList.add(diam);
		
		Feature cont = new Feature();
		cont.setFeature("CONTRAST");
		cont.setName("Contrast");
		cont.setShortName("Constrast");
		cont.setDimension("NONE");
		cont.setIsInt(false);
		
		spotList.add(cont);
		
		Feature snr = new Feature();
		snr.setFeature("SNR");
		snr.setName("Signal/Noise ratio");
		snr.setShortName("SNR");
		snr.setDimension("NONE");
		snr.setIsInt(false);
		
		spotList.add(snr);
		
		spotFeatures.setList(spotList);
		
		
		ArrayList<Feature> edgeList = new ArrayList<Feature>();
		
		Feature source = new Feature();
		source.setFeature("SPOT_SOURCE_ID");
		source.setName("Source spot ID");
		source.setShortName("Source ID");
		source.setDimension("NONE");
		source.setIsInt(true);
		
		edgeList.add(source);
		
		Feature target = new Feature();
		target.setFeature("SPOT_TARGET_ID");
		target.setName("Target spot ID");
		target.setShortName("Target ID");
		target.setDimension("NONE");
		target.setIsInt(true);
		
		edgeList.add(target);
		
		Feature cost = new Feature();
		cost.setFeature("LINK_COST");
		cost.setName("Link cost");
		cost.setShortName("Cost");
		cost.setDimension("NONE");
		cost.setIsInt(false);
		
		edgeList.add(cost);
		
		Feature time = new Feature();
		time.setFeature("EDGE_TIME");
		time.setName("Time (mean)");
		time.setShortName("T");
		time.setDimension("TIME");
		time.setIsInt(false);
		
		edgeList.add(time);
		
		Feature X = new Feature();
		X.setFeature("EDGE_X_LOCATION");
		X.setName("X Location (mean)");
		X.setShortName("X");
		X.setDimension("POSITION");
		X.setIsInt(false);
		
		edgeList.add(X);
		
		Feature Y = new Feature();
		Y.setFeature("EDGE_Y_LOCATION");
		Y.setName("Y Location (mean)");
		Y.setShortName("Y");
		Y.setDimension("POSITION");
		Y.setIsInt(false);
		
		edgeList.add(Y);
		
		Feature Z = new Feature();
		Z.setFeature("EDGE_Z_LOCATION");
		Z.setName("Z Location (mean)");
		Z.setShortName("Z");
		Z.setDimension("POSITION");
		Z.setIsInt(false);
		
		edgeList.add(Z);
		
		Feature velocity = new Feature();
		velocity.setFeature("VELOCITY");
		velocity.setName("Velocity");
		velocity.setShortName("V");
		velocity.setDimension("VELOCITY");
		velocity.setIsInt(false);
		
		edgeList.add(velocity);
		
		Feature d = new Feature();
		d.setFeature("DISPLACEMENT");
		d.setName("Displacement");
		d.setShortName("D");
		d.setDimension("LENGTH");
		d.setIsInt(false);
		
		edgeList.add(d);
		
		Feature colo = new Feature();
		colo.setFeature("MANUAL_COLOR");
		colo.setName("Manual edge color");
		colo.setShortName("Edge color");
		colo.setDimension("NONE");
		colo.setIsInt(true);
		
		edgeList.add(colo);
		
		edgeFeatures.setList(edgeList);
		
		ArrayList<Feature> trackList = new ArrayList<Feature>();
		
		Feature nSpots = new Feature();
		nSpots.setFeature("NUMBER_SPOTS");
		nSpots.setName("Number of spots in track");
		nSpots.setShortName("N spots");
		nSpots.setDimension("NONE");
		nSpots.setIsInt(true);
		
		trackList.add(nSpots);
		
		Feature nGaps = new Feature();
		nGaps.setFeature("NUMBER_GAPS");
		nGaps.setName("Number of gaps");
		nGaps.setShortName("Gaps");
		nGaps.setDimension("NONE");
		nGaps.setIsInt(true);
		
		trackList.add(nGaps);
		
		Feature gap = new Feature();
		gap.setFeature("LONGEST_GAP");
		gap.setName("Longest gap");
		gap.setShortName("Longest gap");
		gap.setDimension("NONE");
		gap.setIsInt(true);
		
		trackList.add(gap);
		
		Feature nSplits = new Feature();
		nSplits.setFeature("NUMBER_SPLITS");
		nSplits.setName("Number of split events");
		nSplits.setShortName("Splits");
		nSplits.setDimension("NONE");
		nSplits.setIsInt(true);
		
		trackList.add(nSplits);
		
		Feature nMerges = new Feature();
		nMerges.setFeature("NUMBER_MERGES");
		nMerges.setName("Number of merge events");
		nMerges.setShortName("Merges");
		nMerges.setDimension("NONE");
		nMerges.setIsInt(true);
		
		trackList.add(nMerges);
		
		Feature complex = new Feature();
		complex.setFeature("NUMBER_COMPLEX");
		complex.setName("Complex points");
		complex.setShortName("Complex");
		complex.setDimension("NONE");
		complex.setIsInt(true);
		
		trackList.add(complex);
		
		Feature duration = new Feature();
		duration.setFeature("TRACK_DURATION");
		duration.setName("Duration of track");
		duration.setShortName("Duration");
		duration.setDimension("TIME");
		duration.setIsInt(false);
		
		trackList.add(duration);
		
        Feature start = new Feature();
        start.setFeature("TRACK_START");
        start.setName("Track start");
        start.setShortName("T start");
        start.setDimension("TIME");
        start.setIsInt(false);
        
        trackList.add(start);
        
        Feature stop = new Feature();
        stop.setFeature("TRACK_STOP");
        stop.setName("Track stop");
        stop.setShortName("T stop");
        stop.setDimension("TIME");
        stop.setIsInt(false);
        
        trackList.add(stop);
        
        Feature dis = new Feature();
        dis.setFeature("TRACK_DISPLACEMENT");
        dis.setName("Track displacement");
        dis.setShortName("Displacement");
        dis.setDimension("LENGTH");
        dis.setIsInt(false);
		
        trackList.add(dis);
        
        Feature index = new Feature();
		index.setFeature("TRACK_INDEX");
		index.setName("Track index");
		index.setShortName("Index");
		index.setDimension("NONE");
		index.setIsInt(true);
		
		trackList.add(index);
        
		Feature id = new Feature();
		id.setFeature("TRACK_ID");
		id.setName("Track ID");
		id.setShortName("ID");
		id.setDimension("NONE");
		id.setIsInt(true);
		
		trackList.add(id);
		
		Feature x = new Feature();
		x.setFeature("TRACK_X_LOCATION");
		x.setName("X Location (mean)");
		x.setShortName("X");
		x.setDimension("POSITION");
		x.setIsInt(false);
		
		trackList.add(x);
		
		Feature y = new Feature();
		y.setFeature("TRACK_Y_LOCATION");
		y.setName("Y Location (mean)");
		y.setShortName("Y");
		y.setDimension("POSITION");
		y.setIsInt(false);
		
		trackList.add(y);
		
		Feature z = new Feature();
		z.setFeature("TRACK_Z_LOCATION");
		z.setName("Z Location (mean)");
		z.setShortName("Z");
		z.setDimension("POSITION");
		z.setIsInt(false);
		
		trackList.add(z);
		
		Feature meanT = new Feature();
		meanT.setFeature("TRACK_MEAN_SPEED");
		meanT.setName("Mean velocity");
		meanT.setShortName("Mean V");
		meanT.setDimension("VELOCITY");
		meanT.setIsInt(false);
		
		trackList.add(meanT);
		
        Feature maxT = new Feature();
        maxT.setFeature("TRACK_MAX_SPEED");
        maxT.setName("Maximal velocity");
        maxT.setShortName("Max V");
        maxT.setDimension("VELOCITY");
        maxT.setIsInt(false);
        
        trackList.add(maxT);
        
        Feature minT = new Feature();
        minT.setFeature("TRACK_MIN_SPEED");
        minT.setName("Minimal velocity");
        minT.setShortName("Min V");
        minT.setDimension("VELOCITY");
        minT.setIsInt(false);
        
        trackList.add(minT);
        
        Feature medianT = new Feature();
        medianT.setFeature("TRACK_MEDIAN_SPEED");
        medianT.setName("Median velocity");
        medianT.setShortName("Median V");
        medianT.setDimension("VELOCITY");
        medianT.setIsInt(false);
        
        trackList.add(medianT);
        
        Feature stdT = new Feature();
        stdT.setFeature("TRACK_STD_SPEED");
        stdT.setName("Velocity standard deviation");
        stdT.setShortName("V std");
        stdT.setDimension("VELOCITY");
        stdT.setIsInt(false);
        
        trackList.add(stdT);
        
        Feature meanQ = new Feature();
        meanQ.setFeature("TRACK_MEAN_QUALITY");
        meanQ.setName("Mean quality");
        meanQ.setShortName("Mean Q");
        meanQ.setDimension("QUALITY");
        meanQ.setIsInt(false);
        
        trackList.add(meanQ);
        
        Feature maxQ = new Feature();
        maxQ.setFeature("TRACK_MAX_QUALITY");
        maxQ.setName("Maximal quality");
        maxQ.setShortName("Max Q");
        maxQ.setDimension("QUALITY");
        maxQ.setIsInt(false);
        
        trackList.add(maxQ);
        
        Feature minQ = new Feature();
        minQ.setFeature("TRACK_MIN_QUALITY");
        minQ.setName("Minimal quality");
        minQ.setShortName("Min Q");
        minQ.setDimension("QUALITY");
        minQ.setIsInt(false);
        
        trackList.add(minQ);
        
        Feature medianQ = new Feature();
        medianQ.setFeature("TRACK_MEDIAN_QUALITY");
        medianQ.setName("Median quality");
        medianQ.setShortName("Median Q");
        medianQ.setDimension("QUALITY");
        medianQ.setIsInt(false);
        
        trackList.add(medianQ);
        
        Feature stdQ = new Feature();
        stdQ.setFeature("TRACK_STD_QUALITY");
        stdQ.setName("Quality standard deviation");
        stdQ.setShortName("Q std");
        stdQ.setDimension("QUALITY");
        stdQ.setIsInt(false);
        
		trackList.add(stdQ);
		
		trackFeatures.setList(trackList);
	}
}
