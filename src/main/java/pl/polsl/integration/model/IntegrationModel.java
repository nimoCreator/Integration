package pl.polsl.integration.model;

import java.util.ArrayList;
import java.util.List;
/**
 * Model class for handling parameters and performing trapezoidal integration.
 * @author Sebastian Legierski InfK4
 * @version 4.0 final
 */
public class IntegrationModel {
    private double width;
    private int divisions;
    private double lowerBound;
    private double upperBound;
    private String function;
    private ModelState modelState = ModelState.empty;
    private IntegrationStrategyEnum integrationStrategy;
    private List<PairRecord> resultTable = new ArrayList<>();
    private IntegrationStrategy strategy;
    
    /**
     * Checks if the model is in a READY state.
     * 
     * @return true if the state is READY, false otherwise.
     */
    public boolean isReady() {
        try {
            this.validate();
        } catch (IntegrationException e) {
            // ignores the exception
        }
        return this.modelState == ModelState.ready;
    }

    /**
     * Validates the model parameters.
     * 
     * @throws IntegrationException if any parameter is invalid.
     */
    public void validate() throws IntegrationException {
        // Ensure lower bound is less than the upper bound, if not, flip the bounds
        if (lowerBound >= upperBound) {
            this.flipBounds();
        }

        // Check if required parameters are set and are valid
        if (function == null || function.isEmpty()) {
            this.modelState = ModelState.incomplete;
            throw new IntegrationException("The function parameter (-f) must be provided and non-empty.");
        }
        if (lowerBound == Double.NaN) {
            this.modelState = ModelState.incomplete;
            throw new IntegrationException("The lower bound (-min) must be provided.");
        }
        if (upperBound == Double.NaN) {
            this.modelState = ModelState.incomplete;
            throw new IntegrationException("The upper bound (-max) must be provided.");
        }

        // Validate polynomial format
        if (!function.matches("([-+]?\\d*\\*?x(\\^\\d+)?)([-+]\\d*\\*?x(\\^\\d+)?)*")) {
            this.modelState = ModelState.error;
            throw new IntegrationException("The provided function string does not match the expected polynomial format.");
        }

        if (integrationStrategy == null) {
            this.modelState = ModelState.error;
            throw new IntegrationException("The integration strategy (either '-d' or '-w') must be set.");
        }

        switch (integrationStrategy) {
            case DivisionsCount, PreciseDivisionsCount:
                if (divisions < 1) {
                    this.modelState = ModelState.error;
                    throw new IntegrationException("Divisions count must be greater than 0.");
                }
                break;
            case TrapesoidWidth, PreciseTrapesoidWidth:
                if (width <= 0) {
                    this.modelState = ModelState.error;
                    throw new IntegrationException("Width must be greater than 0.");
                }
                break;
            default:
                this.modelState = ModelState.error;
                throw new IntegrationException("Invalid integration strategy.");
        }

        this.modelState = ModelState.ready;
    }
    
    /**
     * Sets the integration mode by creating the appropriate strategy.
     * 
     * @param integrationStrategy The integration strategy to use.
     * @throws IllegalArgumentException if the strategy is invalid.
     */
    public void setIntegrationStrategy(IntegrationStrategyEnum integrationStrategy) throws IllegalArgumentException
    {
        this.integrationStrategy = integrationStrategy; 
        switch (integrationStrategy) {
            case IntegrationStrategyEnum.DivisionsCount -> this.setStrategy(new IntegrationStrategyDivision());
            case IntegrationStrategyEnum.TrapesoidWidth -> this.setStrategy(new IntegrationStrategyWidth());
            case IntegrationStrategyEnum.PreciseDivisionsCount -> this.setStrategy(new IntegrationStrategyPreciseDivision());
            case IntegrationStrategyEnum.PreciseTrapesoidWidth -> this.setStrategy(new IntegrationStrategyPreciseWidth());
            default -> throw new IllegalArgumentException("Invalid integration strategy: " + integrationStrategy);
        }
    }


    /**
     * Sets the integration strategy.
     * 
     * @param strategy The integration strategy to use.
     */
    public void setStrategy(IntegrationStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Flips the lower and upper bounds if needed.
     */
    public void flipBounds() {
        double temp = lowerBound;
        this.lowerBound = upperBound;
        this.upperBound = temp;
    }



    /**
     * Calculates the integration result using the selected strategy.
     * 
     * @return The result of the integration.
     * @throws IntegrationException if no strategy is set.
     */
    public double calculate() throws IntegrationException {
        this.validate();
        if(!this.isReady())
        {
            throw new IntegrationException("Model not ready, fill all required parameters ( DivisionsCount/TrapesoidWidth, LowerBound, UpperBound, Function)");
        }
        if (strategy == null) {
            throw new IntegrationException("Integration strategy not set.");
        }
        double wd;
        if(null == this.integrationStrategy) wd = 0;
        else wd = switch (this.integrationStrategy) {
            case DivisionsCount, PreciseDivisionsCount -> this.divisions;
            case TrapesoidWidth, PreciseTrapesoidWidth -> this.width;
            default -> 0;
        };
        
        return strategy.integrate(wd, lowerBound, upperBound, function, resultTable);
    }

    /**
     * Retries range of integration
     * @return PairRecord with lower and upper bound
     */
    public PairRecord getRange() {
        return new PairRecord(lowerBound, upperBound);
    }
    
    
    
    
    // LAMBOKN'T
    
    /**
    * Gets the width value.
    *
    * @return the width value.
    */
   public double getWidth() {
       return width;
   }

   /**
    * Sets the width value.
    *
    * @param width the width value to set.
    */
   public void setWidth(double width) {
       this.width = width;
   }

   /**
    * Gets the number of divisions.
    *
    * @return the number of divisions.
    */
   public int getDivisions() {
       return divisions;
   }

   /**
    * Sets the number of divisions.
    *
    * @param divisions the number of divisions to set.
    */
   public void setDivisions(int divisions) {
       this.divisions = divisions;
   }

   /**
    * Gets the lower bound value.
    *
    * @return the lower bound value.
    */
   public double getLowerBound() {
       return this.lowerBound;
   }

   /**
    * Sets the lower bound value.
    *
    * @param lowerBound the lower bound value to set.
    */
   public void setLowerBound(double lowerBound) {
       this.lowerBound = lowerBound;
   }

   /**
    * Gets the upper bound value.
    *
    * @return the upper bound value.
    */
   public double getUpperBound() {
       return this.upperBound;
   }

   /**
    * Sets the upper bound value.
    *
    * @param upperBound the upper bound value to set.
    */
   public void setUpperBound(double upperBound) {
       this.upperBound = upperBound;
   }

   /**
    * Gets the mathematical function as a string.
    *
    * @return the function string.
    */
   public String getFunction() {
       return function;
   }

   /**
    * Sets the mathematical function.
    *
    * @param function the function string to set.
    */
   public void setFunction(String function) {
       this.function = function;
   }

   /**
    * Gets the integration strategy used.
    *
    * @return the integration strategy.
    */
   public IntegrationStrategyEnum getIntegrationStrategy() {
       return integrationStrategy;
   }

   /**
    * Gets the result table, which stores the computed results.
    *
    * @return the result table as a list of PairRecord objects.
    */
   public List<PairRecord> getResultTable() {
       return resultTable;
   }

   /**
    * Sets the result table.
    *
    * @param resultTable the result table to set, as a list of PairRecord objects.
    */
   public void setResultTable(List<PairRecord> resultTable) {
       this.resultTable = resultTable;
   }

}

