/* 
 * This file is part of Quelea, free projection software for churches.
 * 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.quelea.windows.lyrics;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import org.quelea.services.notice.NoticeDrawer;
import org.quelea.services.utils.LoggerUtils;
import org.quelea.services.utils.Utils;

/**
 * The canvas where the lyrics / images / media are drawn.
 * <p/>
 * @author Michael
 */
public class DisplayCanvas extends StackPane {

    private static final Logger LOGGER = LoggerUtils.getLogger();
    private boolean cleared;
    private boolean blacked;
    private NoticeDrawer noticeDrawer;
    private boolean stageView;
    private ImageView blackImg;
    private Node background;

    /**
     * @return the background
     */
    public Node getBackground() {
        return background;
    }

    /**
     * @param background the background to set
     */
    public void setBackground(Node background) {
        this.background = background;
    }
    interface CanvasCallback {

        void update();
    }
     public ImageView getNewImageView() {
        ImageView ret = new ImageView(Utils.getImageFromColour(Color.BLACK));
        ret.setFitHeight(getHeight());
        ret.setFitWidth(getWidth());
        StackPane.setAlignment(ret, Pos.CENTER);
        return ret;
    }
    /**
     * Create a new canvas where the lyrics should be displayed.
     * <p/>
     * @param showBorder true if the border should be shown around any text
     * (only if the options say so) false otherwise.
     */
    public DisplayCanvas(boolean showBorder, boolean stageView) {
        setMinHeight(0);
        setMinWidth(0);
        this.stageView = stageView;
        blackImg = new ImageView(Utils.getImageFromColour(Color.BLACK));
        noticeDrawer = new NoticeDrawer(this);
        background = getNewImageView();
        heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                update(null);
            }
        });
        widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                update(null);
            }
        });
    }

    public void update(final CanvasCallback callback) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.update();
                }

                if (blacked) {
                    if (getChildren().contains(getBackground())) {
                        getChildren().add(0, blackImg);
                        getChildren().remove(getBackground());
                    }
                } else {
                    if (!getChildren().contains(getBackground())) {
                        getChildren().remove(blackImg);
                        getChildren().add(0, getBackground());
                    }
                }
                if (getBackground() instanceof ImageView) {
                    ImageView imgBackground = (ImageView) getBackground();
                    imgBackground.setFitHeight(getHeight());
                    imgBackground.setFitWidth(getWidth());
                } else if (getBackground() instanceof MediaView) {
                    MediaView vidBackground = (MediaView) getBackground();
                    vidBackground.setPreserveRatio(false);
                    vidBackground.setFitHeight(getHeight());
                    vidBackground.setFitWidth(getWidth());
                } else {
                    LOGGER.log(Level.WARNING, "BUG: Unrecognised image background");
                }
                blackImg.setFitHeight(getHeight());
                blackImg.setFitWidth(getWidth());
            }
        });
    }

    /**
     * Determine if this canvas is part of a stage view.
     * <p/>
     * @return true if its a stage view, false otherwise.
     */
    public boolean isStageView() {
        return stageView;
    }

    /**
     * Toggle the clearing of this canvas - still leave the background image in
     * place but remove all the text.
     */
    public void toggleClear() {
        cleared ^= true; //invert
        update(null);
    }

    /**
     * Determine whether this canvas is cleared.
     * <p/>
     * @return true if the canvas is cleared, false otherwise.
     */
    public boolean isCleared() {
        return cleared;
    }

    /**
     * Toggle the blacking of this canvas - remove the text and background image
     * (if any) just displaying a black screen.
     */
    public void toggleBlack() {
        blacked ^= true; //invert
        update(null);
    }

    /**
     * Determine whether this canvas is blacked.
     * <p/>
     * @return true if the canvas is blacked, false otherwise.
     */
    public boolean isBlacked() {
        return blacked;
    }

    /**
     * Get the notice drawer, used for drawing notices onto this lyrics canvas.
     * <p/>
     * @return the notice drawer.
     */
    public NoticeDrawer getNoticeDrawer() {
        return noticeDrawer;
    }
}
