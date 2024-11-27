export function transformJsonForBackend(state) {
    const transformedState = JSON.parse(JSON.stringify(state));
    
    // Transform variableTypeRules
    if (transformedState.categories?.variableTypeRules) {
        transformedState.categories.variableTypeRules = transformedState.categories.variableTypeRules.map(rule => ({
            name: rule.header,
            type: rule.type
        }));
    }

    // Transform sizeRules
    if (transformedState.categories?.sizeRules) {
        transformedState.categories.sizeRules = transformedState.categories.sizeRules.map(rule => ({
            name: rule.header,
            size: rule.size
        }));
    }

    // Transform minimumAndMaximumRules
    if (transformedState.categories?.minimumAndMaximumRules) {
        transformedState.categories.minimumAndMaximumRules = transformedState.categories.minimumAndMaximumRules.map(rule => ({
            name: rule.header,
            minValue: rule.minValue,
            maxValue: rule.maxValue
        }));
    }

    // Transform comparisonsWithOtherColumnRules
    if (transformedState.categories?.comparisonsWithOtherColumnRules) {
        transformedState.categories.comparisonsWithOtherColumnRules = transformedState.categories.comparisonsWithOtherColumnRules.map(rule => ({
            comparetorOne: rule.header1,
            comparetorTwo: rule.header2,
            operator: rule.operator === '<' ? 'less_than' : rule.operator === '>' ? 'greater_than' : 'equal_to'
        }));
    }

    return transformedState;
}

export function transformJsonFromBackend(state) {
    const transformedState = JSON.parse(JSON.stringify(state));
    
    // Transform variableTypeRules
    if (transformedState.categories?.variableTypeRules) {
        transformedState.categories.variableTypeRules = transformedState.categories.variableTypeRules.map(rule => ({
            header: rule.name,
            type: rule.type
        }));
    }

    // Transform sizeRules
    if (transformedState.categories?.sizeRules) {
        transformedState.categories.sizeRules = transformedState.categories.sizeRules.map(rule => ({
            header: rule.name,
            size: rule.size
        }));
    }

    // Transform minimumAndMaximumRules
    if (transformedState.categories?.minimumAndMaximumRules) {
        transformedState.categories.minimumAndMaximumRules = transformedState.categories.minimumAndMaximumRules.map(rule => ({
            header: rule.name,
            minValue: rule.minValue,
            maxValue: rule.maxValue
        }));
    }

    // Transform comparisonsWithOtherColumnRules
    if (transformedState.categories?.comparisonsWithOtherColumnRules) {
        transformedState.categories.comparisonsWithOtherColumnRules = transformedState.categories.comparisonsWithOtherColumnRules.map(rule => ({
            header1: rule.comparetorOne,
            header2: rule.comparetorTwo,
            operator: rule.operator === 'less_than' ? '<' : rule.operator === 'greater_than' ? '>' : '='
        }));
    }

    return transformedState;
}

export function transformJsonForAnalysis(state) {
    // Start with the backend transformation
    let transformedState = transformJsonForBackend(state);
    
    // Add any additional transformations needed for analysis
    // For example, you might want to add metadata or adjust formats
    
    transformedState.metadata = {
        timestamp: new Date().toISOString(),
        version: "1.0"
    };
    
    // Ensure the structure matches exactly what the backend expects for analysis
    if (!transformedState.rules) {
        transformedState = { rules: transformedState };
    }
    
    return transformedState;
}