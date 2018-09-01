package com.vimalselvam.cucumber.listener.state;

import com.vimalselvam.cucumber.listener.TestSourcesModel;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ExtentState {
    private ExtentFeature currentFeature;

    private ExtentScenario currentScenario;

    private ExtentStep currentStep;

    private TestSourcesModel testSourcesModel;

    private Map<String, ExtentFeature> featuresByUri;

    public void putFeature(final String uri, final ExtentFeature extentFeature) {
        this.featuresByUri.put(uri, extentFeature);
    }

    public Optional<ExtentFeature> getFeature(final String uri) {
        return Optional.ofNullable(this.featuresByUri.get(uri));
    }

    public TestSourcesModel getTestSourcesModel() {
        return testSourcesModel;
    }

    public ExtentFeature getCurrentFeature() {
        return currentFeature;
    }

    public ExtentScenario getCurrentScenario() {
        return currentScenario;
    }

    public ExtentStep getCurrentStep() {
        return currentStep;
    }

    private ExtentState(final TestSourcesModel testSourcesModel, final Map<String, ExtentFeature> featuresByUri) {
        this.testSourcesModel = testSourcesModel;
        this.featuresByUri = featuresByUri;
    }

    public static final class ExtentStateBuilder {
        private ExtentFeature currentFeature;
        private ExtentScenario currentScenario;
        private ExtentStep currentStep;
        private TestSourcesModel testSourcesModel;
        private Map<String, ExtentFeature> featuresByUri;

        private ExtentStateBuilder() {
        }

        public static ExtentStateBuilder anExtentState() {
            final ExtentStateBuilder extentStateBuilder = new ExtentStateBuilder();
            extentStateBuilder.testSourcesModel = new TestSourcesModel();
            extentStateBuilder.featuresByUri = new ConcurrentHashMap<>();

            return extentStateBuilder;
        }

        public static ExtentStateBuilder anExtentState(final ExtentState existing) {
            return anExtentState()
                    .withTestSourceModel(existing.testSourcesModel)
                    .withFeaturesByUri(existing.featuresByUri)
                    .withCurrentFeature(existing.currentFeature)
                    .withCurrentScenario(existing.currentScenario)
                    .withCurrentStep(existing.currentStep);
        }

        private ExtentStateBuilder withTestSourceModel(final TestSourcesModel testSourceModel) {
            this.testSourcesModel = testSourceModel;
            return this;
        }

        private ExtentStateBuilder withFeaturesByUri(final Map<String, ExtentFeature> featureByUri) {
            this.featuresByUri = featureByUri;
            return this;
        }

        public ExtentStateBuilder withCurrentFeature(final ExtentFeature currentFeature) {
            this.currentFeature = currentFeature;
            return this;
        }

        public ExtentStateBuilder withCurrentScenario(final ExtentScenario currentScenario) {
            this.currentScenario = currentScenario;
            return this;
        }

        public ExtentStateBuilder withCurrentStep(final ExtentStep currentStep) {
            this.currentStep = currentStep;
            return this;
        }


        public ExtentState build() {
            ExtentState exState = new ExtentState(this.testSourcesModel, this.featuresByUri);
            exState.currentStep = this.currentStep;
            exState.currentScenario = this.currentScenario;
            exState.currentFeature = this.currentFeature;
            return exState;
        }
    }
}
