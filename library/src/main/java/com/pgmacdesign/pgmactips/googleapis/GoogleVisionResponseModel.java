package com.pgmacdesign.pgmactips.googleapis;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by pmacdowell on 2018-04-02.
 */

public class GoogleVisionResponseModel {

    @SerializedName("responses")
    private List<VisionResponse> responses;

    public List<VisionResponse> getResponses() {
        return responses;
    }

    public void setResponses(List<VisionResponse> responses) {
        this.responses = responses;
    }

    public static class VisionResponse {
        @SerializedName("textAnnotations")
        private List<TextAnnotations> textAnnotations;
        @SerializedName("fullTextAnnotations")
        private FullTextAnnotations fullTextAnnotations;
        @SerializedName("cropHints")
        private List<CropHints> cropHints;
        @SerializedName("faceAnnotations")
        private List<FaceAnnotations> faceAnnotations;
        @SerializedName("webDetection")
        private WebDetection webDetection;


        public List<FaceAnnotations> getFaceAnnotations() {
            return faceAnnotations;
        }

        public void setFaceAnnotations(List<FaceAnnotations> faceAnnotations) {
            this.faceAnnotations = faceAnnotations;
        }

        public WebDetection getWebDetection() {
            return webDetection;
        }

        public void setWebDetection(WebDetection webDetection) {
            this.webDetection = webDetection;
        }

        public List<CropHints> getCropHints() {
            return cropHints;
        }

        public void setCropHints(List<CropHints> cropHints) {
            this.cropHints = cropHints;
        }

        public List<TextAnnotations> getTextAnnotations() {
            return textAnnotations;
        }

        public void setTextAnnotations(List<TextAnnotations> textAnnotations) {
            this.textAnnotations = textAnnotations;
        }

        public FullTextAnnotations getFullTextAnnotations() {
            return fullTextAnnotations;
        }

        public void setFullTextAnnotations(FullTextAnnotations fullTextAnnotations) {
            this.fullTextAnnotations = fullTextAnnotations;
        }
    }

    public static class FaceAnnotations {
        @SerializedName("headwearLikelihood")
        private String headwearLikelihood;
        @SerializedName("blurredLikelihood")
        private String blurredLikelihood;
        @SerializedName("underExposedLikelihood")
        private String underExposedLikelihood;
        @SerializedName("surpriseLikelihood")
        private String surpriseLikelihood;
        @SerializedName("angerLikelihood")
        private String angerLikelihood;
        @SerializedName("sorrowLikelihood")
        private String sorrowLikelihood;
        @SerializedName("joyLikelihood")
        private String joyLikelihood;
        @SerializedName("landmarkingConfidence")
        private Float landmarkingConfidence;
        @SerializedName("detectionConfidence")
        private Float detectionConfidence;
        @SerializedName("tiltAngle")
        private Float tiltAngle;
        @SerializedName("panAngle")
        private Float panAngle;
        @SerializedName("rollAngle")
        private Float rollAngle;
        @SerializedName("landmarks")
        private List<VisionLandmarks> landmarks;
        @SerializedName("fdBoundingPoly")
        private BoundingBox fdBoundingPoly;
        @SerializedName("boundingPoly")
        private BoundingBox boundingPoly;

        public String getHeadwearLikelihood() {
            return headwearLikelihood;
        }

        public void setHeadwearLikelihood(String headwearLikelihood) {
            this.headwearLikelihood = headwearLikelihood;
        }

        public String getBlurredLikelihood() {
            return blurredLikelihood;
        }

        public void setBlurredLikelihood(String blurredLikelihood) {
            this.blurredLikelihood = blurredLikelihood;
        }

        public String getUnderExposedLikelihood() {
            return underExposedLikelihood;
        }

        public void setUnderExposedLikelihood(String underExposedLikelihood) {
            this.underExposedLikelihood = underExposedLikelihood;
        }

        public String getSurpriseLikelihood() {
            return surpriseLikelihood;
        }

        public void setSurpriseLikelihood(String surpriseLikelihood) {
            this.surpriseLikelihood = surpriseLikelihood;
        }

        public String getAngerLikelihood() {
            return angerLikelihood;
        }

        public void setAngerLikelihood(String angerLikelihood) {
            this.angerLikelihood = angerLikelihood;
        }

        public String getSorrowLikelihood() {
            return sorrowLikelihood;
        }

        public void setSorrowLikelihood(String sorrowLikelihood) {
            this.sorrowLikelihood = sorrowLikelihood;
        }

        public String getJoyLikelihood() {
            return joyLikelihood;
        }

        public void setJoyLikelihood(String joyLikelihood) {
            this.joyLikelihood = joyLikelihood;
        }

        public Float getLandmarkingConfidence() {
            return landmarkingConfidence;
        }

        public void setLandmarkingConfidence(Float landmarkingConfidence) {
            this.landmarkingConfidence = landmarkingConfidence;
        }

        public Float getDetectionConfidence() {
            return detectionConfidence;
        }

        public void setDetectionConfidence(Float detectionConfidence) {
            this.detectionConfidence = detectionConfidence;
        }

        public Float getTiltAngle() {
            return tiltAngle;
        }

        public void setTiltAngle(Float tiltAngle) {
            this.tiltAngle = tiltAngle;
        }

        public Float getPanAngle() {
            return panAngle;
        }

        public void setPanAngle(Float panAngle) {
            this.panAngle = panAngle;
        }

        public Float getRollAngle() {
            return rollAngle;
        }

        public void setRollAngle(Float rollAngle) {
            this.rollAngle = rollAngle;
        }

        public List<VisionLandmarks> getLandmarks() {
            return landmarks;
        }

        public void setLandmarks(List<VisionLandmarks> landmarks) {
            this.landmarks = landmarks;
        }

        public BoundingBox getFdBoundingPoly() {
            return fdBoundingPoly;
        }

        public void setFdBoundingPoly(BoundingBox fdBoundingPoly) {
            this.fdBoundingPoly = fdBoundingPoly;
        }

        public BoundingBox getBoundingPoly() {
            return boundingPoly;
        }

        public void setBoundingPoly(BoundingBox boundingPoly) {
            this.boundingPoly = boundingPoly;
        }
    }

    public static class VisionLandmarks {
        /**
         * Enum of values, IE: LEFT_EYE, LEFT_OF_LEFT_EYEBROW, NOSE_TIP, UPPER_LIP, etc
         */
        @SerializedName("type")
        private String type;
        @SerializedName("position")
        private Position position;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Position getPosition() {
            return position;
        }

        public void setPosition(Position position) {
            this.position = position;
        }
    }

    public static class WebDetection {
        @SerializedName("webEntities")
        private List<WebEntities> webEntities;
        @SerializedName("partialMatchingImages")
        private List<PartialMatchingImages> partialMatchingImages;
        @SerializedName("pagesWithMatchingImages")
        private List<PagesWithMatchingImages> pagesWithMatchingImages;
        @SerializedName("bestGuessLabels")
        private List<BestGuessLabels> bestGuessLabels;

        public List<WebEntities> getWebEntities() {
            return webEntities;
        }

        public void setWebEntities(List<WebEntities> webEntities) {
            this.webEntities = webEntities;
        }

        public List<PartialMatchingImages> getPartialMatchingImages() {
            return partialMatchingImages;
        }

        public void setPartialMatchingImages(List<PartialMatchingImages> partialMatchingImages) {
            this.partialMatchingImages = partialMatchingImages;
        }

        public List<PagesWithMatchingImages> getPagesWithMatchingImages() {
            return pagesWithMatchingImages;
        }

        public void setPagesWithMatchingImages(List<PagesWithMatchingImages> pagesWithMatchingImages) {
            this.pagesWithMatchingImages = pagesWithMatchingImages;
        }

        public List<BestGuessLabels> getBestGuessLabels() {
            return bestGuessLabels;
        }

        public void setBestGuessLabels(List<BestGuessLabels> bestGuessLabels) {
            this.bestGuessLabels = bestGuessLabels;
        }
    }

    public static class WebEntities {
        @SerializedName("entityId")
        private String entityId;
        @SerializedName("score")
        private Float score;
        @SerializedName("description")
        private String description;

        public String getEntityId() {
            return entityId;
        }

        public void setEntityId(String entityId) {
            this.entityId = entityId;
        }

        public Float getScore() {
            return score;
        }

        public void setScore(Float score) {
            this.score = score;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class PartialMatchingImages {
        @SerializedName("url")
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class PagesWithMatchingImages {
        @SerializedName("url")
        private String url;
        @SerializedName("pageTitle")
        private String pageTitle;
        @SerializedName("partialMatchingImages")
        private List<PartialMatchingImages> partialMatchingImages;

        public String getPageTitle() {
            return pageTitle;
        }

        public void setPageTitle(String pageTitle) {
            this.pageTitle = pageTitle;
        }

        public List<PartialMatchingImages> getPartialMatchingImages() {
            return partialMatchingImages;
        }

        public void setPartialMatchingImages(List<PartialMatchingImages> partialMatchingImages) {
            this.partialMatchingImages = partialMatchingImages;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class BestGuessLabels {
        @SerializedName("label")
        private String label;
        @SerializedName("languageCode")
        private String languageCode;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getLanguageCode() {
            return languageCode;
        }

        public void setLanguageCode(String languageCode) {
            this.languageCode = languageCode;
        }
    }

    public static class CropHints {
        @SerializedName("boundingPoly")
        private List<BoundingBox> boundingPoly;
        @SerializedName("confidence")
        private Float confidence;
        @SerializedName("importanceFraction")
        private Float importanceFraction;

        public List<BoundingBox> getBoundingPoly() {
            return boundingPoly;
        }

        public void setBoundingPoly(List<BoundingBox> boundingPoly) {
            this.boundingPoly = boundingPoly;
        }

        public Float getConfidence() {
            return confidence;
        }

        public void setConfidence(Float confidence) {
            this.confidence = confidence;
        }

        public Float getImportanceFraction() {
            return importanceFraction;
        }

        public void setImportanceFraction(Float importanceFraction) {
            this.importanceFraction = importanceFraction;
        }
    }

    public static class TextAnnotations {
        @SerializedName("locale")
        private String locale;
        @SerializedName("description")
        private String description;
        @SerializedName("boundingPoly")
        private List<BoundingBox> boundingPoly;

        public String getLocale() {
            return locale;
        }

        public void setLocale(String locale) {
            this.locale = locale;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<BoundingBox> getBoundingPoly() {
            return boundingPoly;
        }

        public void setBoundingPoly(List<BoundingBox> boundingPoly) {
            this.boundingPoly = boundingPoly;
        }
    }

    public static class FullTextAnnotations {
        @SerializedName("text")
        private String text;
        @SerializedName("pages")
        private List<Pages> pages;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public List<Pages> getPages() {
            return pages;
        }

        public void setPages(List<Pages> pages) {
            this.pages = pages;
        }
    }

    public static class Pages {
        @SerializedName("width")
        private Integer width;
        @SerializedName("height")
        private Integer height;
        @SerializedName("property")
        private VisionProperty property;
        @SerializedName("blocks")
        private List<VisionBlocks> blocks;

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }

        public VisionProperty getProperty() {
            return property;
        }

        public void setProperty(VisionProperty property) {
            this.property = property;
        }

        public List<VisionBlocks> getBlocks() {
            return blocks;
        }

        public void setBlocks(List<VisionBlocks> blocks) {
            this.blocks = blocks;
        }
    }

    public static class VisionBlocks {
        @SerializedName("boundingBox")
        private BoundingBox boundingBox;
        @SerializedName("paragraphs")
        private BoundingBox paragraphs;
        @SerializedName("blockType")
        private String blockType;
        @SerializedName("confidence")
        private Float confidence;

        public BoundingBox getParagraphs() {
            return paragraphs;
        }

        public void setParagraphs(BoundingBox paragraphs) {
            this.paragraphs = paragraphs;
        }

        public String getBlockType() {
            return blockType;
        }

        public void setBlockType(String blockType) {
            this.blockType = blockType;
        }

        public Float getConfidence() {
            return confidence;
        }

        public void setConfidence(Float confidence) {
            this.confidence = confidence;
        }

        public BoundingBox getBoundingBox() {
            return boundingBox;
        }

        public void setBoundingBox(BoundingBox boundingBox) {
            this.boundingBox = boundingBox;
        }
    }

    public static class BoundingBox {
        @SerializedName("vertices")
        private List<Vertices> vertices;

        public List<Vertices> getVertices() {
            return vertices;
        }

        public void setVertices(List<Vertices> vertices) {
            this.vertices = vertices;
        }
    }

    public static class Position {
        @SerializedName("x")
        private Float x;
        @SerializedName("y")
        private Float y;
        @SerializedName("z")
        private Float z;

        public Float getX() {
            return x;
        }

        public void setX(Float x) {
            this.x = x;
        }

        public Float getY() {
            return y;
        }

        public void setY(Float y) {
            this.y = y;
        }

        public Float getZ() {
            return z;
        }

        public void setZ(Float z) {
            this.z = z;
        }
    }

    public static class Vertices {
        @SerializedName("x")
        private Integer x;
        @SerializedName("y")
        private Integer y;

        public Integer getX() {
            return x;
        }

        public void setX(Integer x) {
            this.x = x;
        }

        public Integer getY() {
            return y;
        }

        public void setY(Integer y) {
            this.y = y;
        }
    }

    public static class VisionProperty {
        @SerializedName("detectedLanguages")
        private List<DetectedLanguages> detectedLanguages;

        public List<DetectedLanguages> getDetectedLanguages() {
            return detectedLanguages;
        }

        public void setDetectedLanguages(List<DetectedLanguages> detectedLanguages) {
            this.detectedLanguages = detectedLanguages;
        }
    }

    public static class DetectedLanguages {
        @SerializedName("languageCode")
        private String languageCode;
        @SerializedName("confidence")
        private Float confidence;

        public String getLanguageCode() {
            return languageCode;
        }

        public void setLanguageCode(String languageCode) {
            this.languageCode = languageCode;
        }

        public Float getConfidence() {
            return confidence;
        }

        public void setConfidence(Float confidence) {
            this.confidence = confidence;
        }
    }
}
