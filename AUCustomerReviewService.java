package de.hybris.platform.customerreview.impl;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import de.hybris.platform.customerreview.CustomerReviewModel;
import de.hybris.platform.customerreview.CustomerReviewService;
import de.hybris.platform.customerreview.LanguageModel;
import de.hybris.platform.customerreview.ProductModel;
import de.hybris.platform.customerreview.UserModel;
import de.hybris.platform.customerreview.jalo.CustomerReview;
import de.hybris.platform.customerreview.jalo.CustomerReviewManager;
import de.hybris.platform.customerreview.constants;
import de.hybris.platform.customerreview.constants.CustomerReviewConstants;
import de.hybris.platform.customerreview.dao.CustomerReviewDao;

public class AUCustomerReviewService extends AbstractBusinessService implements CustomerReviewService  {

	String BAD_WORD = "Comment censored - contains bad word";
	String RATING_NOT_VALID = "Rating not valid - less than minimal"; 
	
	private CustomerReviewDao customerReviewDao;
	  
	protected CustomerReviewDao getCustomerReviewDao()
	{
		return this.customerReviewDao;
	}
	 
	 @Required
	/public void setCustomerReviewDao(CustomerReviewDao customerReviewDao)
	{
		 this.customerReviewDao = customerReviewDao;
	}
	
	private String fileName;   //  property 
	   
	public String getFileName()
	{
		return this.fileName;
	}
  
    public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	@Override
	public CustomerReviewModel createCustomerReview(Double rating, String headline, String comment, UserModel user, ProductModel product)  throws Exception 
	{
	     
		ArrayList<String> cursedList = getCursedList();
		if(cursedList.contains(comment))  {
			throw(new Exception(BAD_WORD));   
		}

		if(rating < CustomerReviewConstants.DEFAULTS.MINIMAL_RATING)  {
			throw(new Exception(RATING_NOT_VALID));	
		}
		
		CustomerReview review = CustomerReviewManager.getInstance().createCustomerReview(rating, headline, comment, 
	    		        (User)getModelService().getSource(user), (Product)getModelService().getSource(product));
	    return (CustomerReviewModel)getModelService().get(review);
					
	}

	@Override
	public void updateCustomerReview(CustomerReviewModel paramCustomerReviewModel, UserModel paramUserModel,
			ProductModel paramProductModel) {
		model.setProduct(product);
		model.setUser(user);
		getModelService().save(model);
	}

	@Override
	public List<CustomerReviewModel> getAllReviews(ProductModel paramProductModel) {
		    List<CustomerReview> reviews = CustomerReviewManager.getInstance().getAllReviews(
			(Product)getModelService().getSource(product));
			return (List)getModelService().getAll(reviews, new ArrayList());
	}

	@Override
	public Double getAverageRating(ProductModel paramProductModel) {
		 return CustomerReviewManager.getInstance().getAverageRating((Product)getModelService().getSource(product));
	}

	@Override
	public Integer getNumberOfReviews(ProductModel paramProductModel) {
		return CustomerReviewManager.getInstance().getNumberOfReviews((Product)getModelService().getSource(product));
	}

	@Override
	public List<CustomerReviewModel> getReviewsForProduct(ProductModel paramProductModel) {
		ServicesUtil.validateParameterNotNullStandardMessage("product", product);
		return getCustomerReviewDao().getReviewsForProduct(product);
		return null;
	}

	@Override
	public List<CustomerReviewModel> getReviewsForProductAndLanguage(ProductModel paramProductModel,
			LanguageModel paramLanguageModel) {
		ServicesUtil.validateParameterNotNullStandardMessage("product", product);
		ServicesUtil.validateParameterNotNullStandardMessage("language", language);
		return getCustomerReviewDao().getReviewsForProductAndLanguage(product, language);
	}

	//  Counts product’s total number of customer reviews whose ratings are within 
	//  a given range (inclusive)
	public Integer getAllReviewsWithinRange(ProductModel productModel, Integer min, Integer max ) {
	    List<CustomerReview> reviews = getReviewsForProduct(productModel);
	    List <CustomerReview> reviewsInRange = new ArrayList <CustomerReview>();
	    for(CustomerReview review : reviews  ) {
	    	if((review.getRating() >= min) && (review.getRating() <= max))  {
	    		reviewsInRange.add(review);
	    	}
	    }	
	    return reviewsInRange.size();
	}
	
	//  Get a listed of cursed words prohibited in comments from file
	private ArrayList<String> getCursedList(String fileName)  {
		ArrayList<String> cursedList = new ArrayList<String>();
		Scanner scan = new Scanner(fileName);
	    while(scan.hasNextLine()){
	    	cursedList.add(scan.nextLine());
	    }
	    scan.close();
		return cursedList;
	}
	
}
