package com.esri.geoevent.solutions.processor.geometry;

import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.ges.spatial.GeometryType;

public class GeometryUtility {

	public GeometryUtility() {}
	
	public static double Geo2Arithmetic(double inAngle)
	{
		return _geo2Arithmetic(inAngle);
	}
	
	private static double _geo2Arithmetic(double inAngle)
	{
		return 360.0-(inAngle+270.0)%360.0;
	}
	
	public static Point Rotate(Point center, Point inPt, double rotationAngle)
	{
		return _rotate(center, inPt, rotationAngle);
	}
	
	public static String parseGeometryType(GeometryType t)
	{
		return _parseGeometryType(t);
	}
	
	private static String _parseGeometryType(GeometryType t)
	{
		String type = null;
		if(t == GeometryType.Point)
		{
			type = "esriGeometryPoint";
		}
		else if (t==GeometryType.Polyline)
		{
			type = "esriGeometryPolyline";
		}
		else if (t==GeometryType.Polygon)
		{
			type = "esriGeometryPolygon";
		}
		else if (t==GeometryType.MultiPoint)
		{
			type = "esriGeometryMultiPoint";
		}
		return type;
	}
	
	private static Point _rotate(Point center, Point inPt, double ra)
	{
		double x = inPt.getX();
		double y = inPt.getY();
		double cx = center.getX();
		double cy = center.getY();
		double cosra = Math.cos(ra);
		double sinra = Math.sin(ra);
		double rx = cx + cosra * (x - cx) - sinra * (y - cy);
		double ry = cy + sinra * (x - cx) + cosra * (y-cy);
		Point rPt = new Point(rx,ry);
		return rPt;
	}
	
	public Polygon GenerateEllipse(Point center, double majorAxis, double minorAxis, double ra)
	{
		Polygon ellipse = new Polygon();
		for (int i = 0; i < 360; ++i)
		{
			double theta = Math.toRadians(i);
			Point p = ellipsePtFromAngle(center, majorAxis, minorAxis, theta);
			p = GeometryUtility.Rotate(center, p, ra);
			if (i == 0) {
				ellipse.startPath(p);
			}
			else{
				ellipse.lineTo(p);
			}
		}
		ellipse.closeAllPaths();
		return ellipse;
	}
	
	private Point ellipsePtFromAngle(Point center, double rh, double rv, double angle)
	{
		double x = center.getX();
		double y = center.getY();
		double c = Math.cos(angle);
		double s = Math.sin(angle);
		double ta = s/c;
		double tt = ta * (rh/rv);
		double d = 1.0 / Math.sqrt(1.0 + Math.pow(tt, 2));
		double ex = x + Math.copySign(rh*d, c);
		double ey = y + Math.copySign(rv * tt * d, s);
		return new Point(ex,ey);
		
	}

}
