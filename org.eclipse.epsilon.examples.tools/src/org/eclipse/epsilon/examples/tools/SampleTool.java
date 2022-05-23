package org.eclipse.epsilon.examples.tools;

import java.util.List;
import java.util.ArrayList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import com.google.common.collect.Collections2;

public class SampleTool {

	  public List<DynamicEObjectImpl> listOfUsedObjects = new ArrayList<DynamicEObjectImpl>();
	  
	  
	  
	  protected String name;

	  public void setName(String name) {
	    this.name = name;
	  }

	  public String getName() {
	    return name;
	  }

	  public String sayHello() {
	    return "Hello " + name;
	  }

	  public DynamicEObjectImpl singleElement(List<DynamicEObjectImpl> list, int i) {
		  return list.get(i);
	  }
	  
	  public List<EObject> references(DynamicEObjectImpl object){
		  return object.eCrossReferences();
	  }
	  
	  public List<EObject> sortClassFromList(List<EObject> list,String classname){
		  List<EObject> sensorList = new ArrayList<EObject>();
		  for (EObject object : list) {
			  if (object.eClass().getName().contentEquals(classname))
				  sensorList.add(object);
			  
		  }
		  return sensorList;
	  }
	  
	  public boolean sensorsCoverBiomarkers(List<DynamicEObjectImpl> biomarkerList,
			  List<DynamicEObjectImpl> sensorList, String deviceType) {
		  if(biomarkerList.size() == 0)
			  return true;
		  if(sensorList.size() == 0)
			  return false;
		  
		  assert singleElement(biomarkerList, 0).eClass().getName().contentEquals("Biomarker");
//		  assert singleElement(sensorList, 0).eClass().getName().contentEquals("Sensor");
		  
		  
		  for (DynamicEObjectImpl biomarker : biomarkerList) {
			  boolean covered = false;
			  List<EObject> sensorsTreatingBiomarker = sortClassFromList(references(biomarker), deviceType);
			  for (EObject sensor : sensorsTreatingBiomarker) {
				  if (sensorList.contains(sensor)) {
					  covered = true;
				  }
			  }
			  if (!covered)
				  return false;
		  }
		  
		  
		  return true;
	  }
	  
	  public List<DynamicEObjectImpl> findLowestCostSensorMapping(List<List<DynamicEObjectImpl>> sensorMappings, String qualityType){
		  int minSoFar = Integer.MAX_VALUE;
		  List<DynamicEObjectImpl> listWithMinSoFar = new ArrayList<DynamicEObjectImpl>();
		  
		  for (List<DynamicEObjectImpl> sensorMapping : sensorMappings) {
			  int total = Integer.MAX_VALUE;
			  for (DynamicEObjectImpl sensor : sensorMapping) {
				  List<EObject> qualities = sortClassFromList(sensor.eContents(),"Quality");
				  for (EObject quality : qualities) {
					  EStructuralFeature feature = quality.eClass().getEStructuralFeature("qualityType");
					  if (quality.eGet(feature).toString().contains(qualityType)) {
						  
						  System.out.println("NO WAY: qualityType");
						  Object shouldBeInt = quality.eGet(quality.eClass().getEStructuralFeature("value"));
						  if (total == Integer.MAX_VALUE) {
							  total = (Integer) shouldBeInt;
						  }
						  else {
							  total = total + (Integer) shouldBeInt;
						  }
						  
						  
						  
					  }
					  
				  }
					  
			  }
			  if (total < minSoFar) {
				  minSoFar = total;
			  	  listWithMinSoFar = sensorMapping;
			  }
				  
		  }
		  if (listWithMinSoFar == null)
			  System.out.println("NULL BAD BAD");
		  return listWithMinSoFar;
	  }
	  
	  public List<DynamicEObjectImpl> generateSensorListMapping(List<DynamicEObjectImpl> biomarkerList
			  ,String deviceType, String qualityType, String astronautName){
		  System.out.println("CALLED");
		  assert singleElement(biomarkerList, 0).eClass().getName().contentEquals("Biomarker");
		 // assert singleElement(sensorList, 0).eClass().getName().contentEquals("Sensor");
		  
		  List<DynamicEObjectImpl> sensorOutputList = new ArrayList<DynamicEObjectImpl>();
		  
		  List<DynamicEObjectImpl> biomarkerWorkingSet = new ArrayList<DynamicEObjectImpl>(biomarkerList);
		  List<DynamicEObjectImpl> sensorWorkingSet = new ArrayList<DynamicEObjectImpl>();
		  for (DynamicEObjectImpl biomarker : biomarkerList) {
			  List<EObject> sensorsTreatingBiomarker = sortClassFromList(references(biomarker), deviceType);
			  if (sensorsTreatingBiomarker.size() == 0) {
				  //THROW ERROR- UNTreated
				  //Shouldn't even be possible because of metamodel
			  }
			  else if (sensorsTreatingBiomarker.size() == 1) {
				  
				  DynamicEObjectImpl sensor = (DynamicEObjectImpl) sensorsTreatingBiomarker.get(0);
			//	  biomarkerWorkingSet.remove(biomarker);
				  //Can only be treated by one sensor
				  if (!listOfUsedObjects.contains(sensor)) {
					  biomarkerWorkingSet.removeAll(sortClassFromList(references(sensor),"Biomarker"));
					  sensorWorkingSet.remove(sensor);
					  sensorOutputList.add(sensor);
					  listOfUsedObjects.add(sensor);
					  //
					  if (biomarkerWorkingSet.size() == 0) {
						  break;
					  } 
				  }
				  
				 
			  }
			  else {
				  for (EObject sensor : sensorsTreatingBiomarker) {
					  if (!listOfUsedObjects.contains(sensor)) {
						  sensorWorkingSet.add((DynamicEObjectImpl) sensor);
					  }
					  
				  }
			  }
		  }
		  System.out.println("Here");
		  if (biomarkerWorkingSet.size() > 0) {
			  Subsets<DynamicEObjectImpl> s = new Subsets<DynamicEObjectImpl>();
			  
			  List<List<DynamicEObjectImpl>> possibleSensorSets = new ArrayList<List<DynamicEObjectImpl>>();
			  
			  
			  
			  for (List<DynamicEObjectImpl> sensorSetToConsider : s.allSubSets(sensorWorkingSet)) {
				  if (!sensorSetToConsider.removeAll(listOfUsedObjects) &&
						  sensorsCoverBiomarkers(biomarkerWorkingSet,sensorSetToConsider,deviceType)
						   ) {
					  possibleSensorSets.add(sensorSetToConsider);
				  }
			  }
			  
			  sensorOutputList.addAll(findLowestCostSensorMapping(possibleSensorSets, qualityType));
		//	  sensorOutputList.add(null);
		  }
		  else {
			  
		  }
		  
		  
		  List<DynamicEObjectImpl> clonedSensorOutputList = new ArrayList<DynamicEObjectImpl>();
		  for (DynamicEObjectImpl sensor : sensorOutputList) {
			  DynamicEObjectImpl sensorCopy =  EcoreUtil.copy(sensor);
			 String originalName =  sensorCopy.eGet(sensorCopy.eClass().getEStructuralFeature("name")).toString();
			 String newName = originalName.concat("_").concat((astronautName).toUpperCase());
			 sensorCopy.eSet(sensorCopy.eClass().getEStructuralFeature("name"), newName);
			 clonedSensorOutputList.add(sensorCopy);
			 }
		  
		  listOfUsedObjects.addAll(sensorOutputList);
		  return clonedSensorOutputList;
	  }
	  
	  public List<DynamicEObjectImpl> combineLists (List<DynamicEObjectImpl> list1,
			  List<DynamicEObjectImpl> list2){
		  List<DynamicEObjectImpl> listToReturn = new ArrayList<DynamicEObjectImpl>();
		  listToReturn.addAll(list1);
		  listToReturn.addAll(list2);
		  return listToReturn;
		  
	  }
	 
	  
  public class Subsets<T> {
	    public List<List<T>> allSubSets(List<T> list) {
	        List<List<T>> out = new ArrayList<List<T>>();
	        for(int i=1; i<=list.size(); i++) {
	            List<List<T>> outAux = this.subSets(list, i);
	            out.addAll(outAux);
	        }
	        return out;
	    }

	    private List<List<T>> subSets(List<T> list, int size) {
	        List<List<T>> out = new ArrayList<List<T>>();
	        for(int i=0; i<list.size()-size+1;i++) {
	            List<T> subset = new ArrayList<T>();
	            for (int j=i;j<i+size-1;j++) {
	                subset.add(list.get(j));
	            }
	            if (!(size==1 && i>0)) {
	                for (int j=i+size-1;j<list.size();j++) {
	                    List<T> newsubset = new ArrayList<T>(subset);
	                    newsubset.add(list.get(j));
	                    out.add(newsubset);
	                }
	            }
	        }
	        return out;
	    }
	}
}