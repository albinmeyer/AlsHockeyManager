/*
 * This file is part of Al's Hockey Manager
 * Copyright (C) 1998-2012 Albin Meyer
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

package ch.hockman.gui;

import java.io.IOException;
import java.util.Stack;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Utility class for changing masks.
 *
 * @author Albin
 *
 */
public class StageHandler {

	private Stack<Stage> sceneStack = new Stack<Stage>();

	public StageHandler(Stage primaryStage, String primaryFilename) {
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource(primaryFilename));
			Scene scene = new Scene(root);
			scene.getStylesheets().addAll(
					StageHandler.class.getResource("controlStyle.css")
							.toExternalForm());
			primaryStage.setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		}
		sceneStack.add(primaryStage);
		primaryStage.show();
	}

	private void moveWindowHandler(final Stage dialog) {
		// allow the dialog to be dragged around.
		class Delta {
			double x, y;
		}
		final Node root = dialog.getScene().getRoot();
		final Delta dragDelta = new Delta();
		root.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				// record a delta distance for the drag and drop operation.
				dragDelta.x = dialog.getX() - mouseEvent.getScreenX();
				dragDelta.y = dialog.getY() - mouseEvent.getScreenY();
			}
		});
		root.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				dialog.setX(mouseEvent.getScreenX() + dragDelta.x);
				dialog.setY(mouseEvent.getScreenY() + dragDelta.y);
			}
		});
	}

	public void showModalStageAndWait(String sceneFilename, String sceneName) {
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource(sceneFilename));
			Scene scene = new Scene(root);
			scene.getStylesheets().addAll(
					StageHandler.class.getResource("controlStyle.css")
							.toExternalForm());
			final Stage dialog = new Stage();
			dialog.setTitle(sceneName);
			dialog.initModality(Modality.WINDOW_MODAL);
			dialog.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent e) {
					// window is closed through standard close widget, not
					// through button
					// we have to clean close the modal window
					// otherwise all following windows are not modal anymore

					// to avoid the user to cheat, we exit the whole program
					System.exit(0);
					// TODO: check, whether we are in match window or not
					// in non-match-windows, the user is allowed to quit
					// HockmanMain.stageHandler.closeModalStage();
				}
			});
			Stage ownerStage = sceneStack.lastElement();
			dialog.setX(ownerStage.getX());
			dialog.setY(ownerStage.getY());
			dialog.initOwner(ownerStage);
			dialog.setScene(scene);
			sceneStack.add(dialog);
			moveWindowHandler(dialog);
			dialog.showAndWait(); // blocks until stage.close() was called
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void closeModalStage() {
		Stage stage = sceneStack.pop();
		moveWindowHandler(sceneStack.lastElement());
		stage.close(); // release the block in showAndWait()
	}
}
