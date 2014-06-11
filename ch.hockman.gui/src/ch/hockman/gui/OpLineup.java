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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import ch.hockman.model.position.Position;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Mask showing the lineup of an opponent's team.
 * This is a controller-class referenced by a fxml-file with the UI-Layout.
 *
 * @author Albin
 *
 */
public class OpLineup implements Initializable {

	@FXML
	private ImageView teamImg;

	@FXML
	private Label totalStrengthLbl;

	@FXML
	private Label gamePlayLbl;

	@FXML
	private Label effortLbl;

	@FXML
	private Label defenseLbl;

	@FXML
	private Label offenseLbl;

	@FXML
	private Label blockSelLbl;

	@FXML
	private Label sponsoringLbl;

	@FXML
	private ListView<String> defense1_55;

	@FXML
	private ListView<String> defense2_55;

	@FXML
	private ListView<String> defense3_55;

	@FXML
	private ListView<String> offense1_55;

	@FXML
	private ListView<String> offense2_55;

	@FXML
	private ListView<String> offense3_55;

	@FXML
	private ListView<String> offense4_55;

	@FXML
	private ListView<String> line1_44;

	@FXML
	private ListView<String> line2_44;

	@FXML
	private ListView<String> line1_33;

	@FXML
	private ListView<String> line2_33;

	@FXML
	private ListView<String> line1_pp5;

	@FXML
	private ListView<String> line2_pp5;

	@FXML
	private ListView<String> line1_pp4;

	@FXML
	private ListView<String> line2_pp4;

	@FXML
	private ListView<String> line1_pk4;

	@FXML
	private ListView<String> line2_pk4;

	@FXML
	private ListView<String> line1_pk3;

	@FXML
	private ListView<String> line2_pk3;

	@FXML
	private ListView<String> goalies;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Image image;
		try {
			String url = "file:" + HockmanMain.lineupTeam.getPicPath();
			image = new Image(url);
			if (image.isError()) {
				url = Manager.class.getResource(Manager.L_TEAMPICPATH)
						.toString();
				image = new Image(url);
			}
		} catch (IllegalArgumentException ex) {
			String url = Manager.class.getResource(Manager.L_TEAMPICPATH)
					.toString();
			image = new Image(url);
		}
		teamImg.setImage(image);
		totalStrengthLbl.setText(Integer.toString(HockmanMain.lineupTeam
				.getStrengthWithMot()));
		ch.hockman.model.Tactics tactics = HockmanMain.lineupTeam.getTactics();
		gamePlayLbl.setText(tactics.getGamePlay().getName());
		effortLbl.setText(tactics.getEffort().getName());
		defenseLbl.setText(tactics.getDefense().getName());
		offenseLbl.setText(tactics.getOffense().getName());
		blockSelLbl.setText(tactics.getBlockSelection().getName());
		sponsoringLbl.setText(HockmanMain.lineupTeam.getSponsoring().getName());
		linesShow();
	}

	@FXML
	private void ok(ActionEvent event) {
		HockmanMain.stageHandler.closeModalStage();
	}

	private void linesShow() {

		// TODO remove copy/paste code with LineUp.java

		// fill the lineup tabs
		ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
				.getLineUp();

		List<String> goalieNames = new ArrayList<String>();
		goalieNames.add(HockmanMain.GetPlayerLineUpString(lineUp.getG().goal1,
				Position.PosID.GOALIE));
		goalieNames.add(HockmanMain.GetPlayerLineUpString(lineUp.getG().goal2,
				Position.PosID.GOALIE));
		ObservableList<String> items = FXCollections
				.observableArrayList(goalieNames);
		goalies.setItems(items);
		HockmanMain.setListViewCellFactory(goalies);

		List<String> e55defense1Names = new ArrayList<String>();
		e55defense1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().d11,
				Position.PosID.DEFENDER));
		e55defense1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().d12,
				Position.PosID.DEFENDER));
		items = FXCollections.observableArrayList(e55defense1Names);
		this.defense1_55.setItems(items);
		HockmanMain.setListViewCellFactory(defense1_55);

		List<String> e55defense2Names = new ArrayList<String>();
		e55defense2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().d21,
				Position.PosID.DEFENDER));
		e55defense2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().d22,
				Position.PosID.DEFENDER));
		items = FXCollections.observableArrayList(e55defense2Names);
		this.defense2_55.setItems(items);
		HockmanMain.setListViewCellFactory(defense2_55);

		List<String> e55defense3Names = new ArrayList<String>();
		e55defense3Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().d31,
				Position.PosID.DEFENDER));
		e55defense3Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().d32,
				Position.PosID.DEFENDER));
		items = FXCollections.observableArrayList(e55defense3Names);
		this.defense3_55.setItems(items);
		HockmanMain.setListViewCellFactory(defense3_55);

		List<String> e55offense1Names = new ArrayList<String>();
		e55offense1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().lw1,
				Position.PosID.LEFTWING));
		e55offense1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().c1,
				Position.PosID.CENTER));
		e55offense1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().rw1,
				Position.PosID.RIGHTWING));
		items = FXCollections.observableArrayList(e55offense1Names);
		this.offense1_55.setItems(items);
		HockmanMain.setListViewCellFactory(offense1_55);

		List<String> e55offense2Names = new ArrayList<String>();
		e55offense2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().lw2,
				Position.PosID.LEFTWING));
		e55offense2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().c2,
				Position.PosID.CENTER));
		e55offense2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().rw2,
				Position.PosID.RIGHTWING));
		items = FXCollections.observableArrayList(e55offense2Names);
		this.offense2_55.setItems(items);
		HockmanMain.setListViewCellFactory(offense2_55);

		List<String> e55offense3Names = new ArrayList<String>();
		e55offense3Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().lw3,
				Position.PosID.LEFTWING));
		e55offense3Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().c3,
				Position.PosID.CENTER));
		e55offense3Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().rw3,
				Position.PosID.RIGHTWING));
		items = FXCollections.observableArrayList(e55offense3Names);
		this.offense3_55.setItems(items);
		HockmanMain.setListViewCellFactory(offense3_55);

		List<String> e55offense4Names = new ArrayList<String>();
		e55offense4Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().lw4,
				Position.PosID.LEFTWING));
		e55offense4Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().c4,
				Position.PosID.CENTER));
		e55offense4Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().rw4,
				Position.PosID.RIGHTWING));
		items = FXCollections.observableArrayList(e55offense4Names);
		this.offense4_55.setItems(items);
		HockmanMain.setListViewCellFactory(offense4_55);

		List<String> e44line1Names = new ArrayList<String>();
		e44line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE44().d11));
		e44line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE44().d12));
		e44line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE44().f11));
		e44line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE44().f12));
		items = FXCollections.observableArrayList(e44line1Names);
		this.line1_44.setItems(items);
		HockmanMain.setListViewCellFactory(line1_44);

		List<String> e44line2Names = new ArrayList<String>();
		e44line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE44().d21));
		e44line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE44().d22));
		e44line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE44().f21));
		e44line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE44().f22));
		items = FXCollections.observableArrayList(e44line2Names);
		this.line2_44.setItems(items);
		HockmanMain.setListViewCellFactory(line2_44);

		List<String> e33line1Names = new ArrayList<String>();
		e33line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE33().d11));
		e33line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE33().d12));
		e33line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE33().f1));
		items = FXCollections.observableArrayList(e33line1Names);
		this.line1_33.setItems(items);
		HockmanMain.setListViewCellFactory(line1_33);

		List<String> e33line2Names = new ArrayList<String>();
		e33line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE33().d21));
		e33line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE33().d22));
		e33line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE33().f2));
		items = FXCollections.observableArrayList(e33line2Names);
		this.line2_33.setItems(items);
		HockmanMain.setListViewCellFactory(line2_33);

		List<String> pp5line1Names = new ArrayList<String>();
		pp5line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp5().d11,
				Position.PosID.DEFENDER));
		pp5line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp5().d12,
				Position.PosID.DEFENDER));
		pp5line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp5().lw1,
				Position.PosID.LEFTWING));
		pp5line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp5().c1,
				Position.PosID.CENTER));
		pp5line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp5().rw1,
				Position.PosID.RIGHTWING));
		items = FXCollections.observableArrayList(pp5line1Names);
		this.line1_pp5.setItems(items);
		HockmanMain.setListViewCellFactory(line1_pp5);

		List<String> pp5line2Names = new ArrayList<String>();
		pp5line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp5().d21,
				Position.PosID.DEFENDER));
		pp5line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp5().d22,
				Position.PosID.DEFENDER));
		pp5line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp5().lw2,
				Position.PosID.LEFTWING));
		pp5line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp5().c2,
				Position.PosID.CENTER));
		pp5line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp5().rw2,
				Position.PosID.RIGHTWING));
		items = FXCollections.observableArrayList(pp5line2Names);
		this.line2_pp5.setItems(items);
		HockmanMain.setListViewCellFactory(line2_pp5);

		List<String> pp4line1Names = new ArrayList<String>();
		pp4line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp4().d11));
		pp4line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp4().d12));
		pp4line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp4().f11));
		pp4line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp4().f12));
		items = FXCollections.observableArrayList(pp4line1Names);
		this.line1_pp4.setItems(items);
		HockmanMain.setListViewCellFactory(line1_pp4);

		List<String> pp4line2Names = new ArrayList<String>();
		pp4line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp4().d21));
		pp4line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp4().d22));
		pp4line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp4().f21));
		pp4line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp4().f22));
		items = FXCollections.observableArrayList(pp4line2Names);
		this.line2_pp4.setItems(items);
		HockmanMain.setListViewCellFactory(line2_pp4);

		List<String> pk4line1Names = new ArrayList<String>();
		pk4line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPk4().d11));
		pk4line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPk4().d12));
		pk4line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPk4().f11));
		pk4line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPk4().f12));
		items = FXCollections.observableArrayList(pk4line1Names);
		this.line1_pk4.setItems(items);
		HockmanMain.setListViewCellFactory(line1_pk4);

		List<String> pk4line2Names = new ArrayList<String>();
		pk4line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPk4().d21));
		pk4line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPk4().d22));
		pk4line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPk4().f21));
		pk4line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPk4().f22));
		items = FXCollections.observableArrayList(pk4line2Names);
		this.line2_pk4.setItems(items);
		HockmanMain.setListViewCellFactory(line2_pk4);

		List<String> pk3line1Names = new ArrayList<String>();
		pk3line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPk3().d11));
		pk3line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPk3().d12));
		pk3line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPk3().f1));
		items = FXCollections.observableArrayList(pk3line1Names);
		this.line1_pk3.setItems(items);
		HockmanMain.setListViewCellFactory(line1_pk3);

		List<String> pk3line2Names = new ArrayList<String>();
		pk3line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPk3().d21));
		pk3line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPk3().d22));
		pk3line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPk3().f2));
		items = FXCollections.observableArrayList(pk3line2Names);
		this.line2_pk3.setItems(items);
		HockmanMain.setListViewCellFactory(line2_pk3);
	}

}
