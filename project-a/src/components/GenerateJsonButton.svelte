<script>
    import { rulesStore } from '../stores/rulesStores.js';
    import { get } from 'svelte/store';
  
    function generateAndLogJson() {
      const currentRules = get(rulesStore);
      
      const finalJson = {
        rules: {
          categories: {
            nullRules: currentRules.categories?.nullRules || [],
            notNullRules: currentRules.categories?.notNullRules || [],
            variableTypeRules: currentRules.categories?.variableTypeRules?.map(rule => ({
              name: rule.name,
              type: rule.type
            })) || [],
            sizeRules: currentRules.categories?.sizeRules?.map(rule => ({
              name: rule.name,
              size: rule.size
            })) || [],
            minimumAndMaximumRules: currentRules.categories?.minimumAndMaximumRules?.map(rule => ({
              name: rule.name,
              minValue: rule.minValue,
              maxValue: rule.maxValue
            })) || [],
            duplicationRules: currentRules.categories?.duplicationRules || [],
            comparisonsWithOtherColumnRules: currentRules.categories?.comparisonsWithOtherColumnRules?.map(rule => ({
              comparetorOne: rule.comparetorOne,
              comparetorTwo: rule.comparetorTwo,
              operator: rule.operator
            })) || [],
            comparisonsWithDateRules: currentRules.categories?.comparisonsWithDateRules?.map(rule => ({
              comparetorOne: rule.comparetorOne,
              comparetorTwo: rule.comparetorTwo,
              operator: rule.operator
            })) || [],
            orderColumnsRules: currentRules.categories?.orderColumnsRules?.map(rule => ({
              columns: rule.columns,
              orderType: rule.orderType
            })) || [],
            rangeWithWordRules: currentRules.categories?.rangeWithWordRules?.map(rule => ({
              rangeColumn: rule.rangeColumn,
              ranges: rule.ranges.map(range => ({
                min: range.min,
                max: range.max,
                type: range.type
              })),
              typeColumn: rule.typeColumn
            })) || [],
            dateRangeRules: currentRules.categories?.dateRangeRules?.map(rule => ({
              startDateColumn: rule.startDateColumn,
              endDateColumn: rule.endDateColumn,
              referenceDateColumn: rule.referenceDateColumn
            })) || []
          }
        },
        report: {
          headers: currentRules.report?.headers || []
        }
      };
  
      console.log('JSON Final:', JSON.stringify(finalJson, null, 2));
    }
  </script>
  
  <button
    on:click={generateAndLogJson}
    class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
    type="button"
  >
    Generar y Mostrar JSON
  </button>