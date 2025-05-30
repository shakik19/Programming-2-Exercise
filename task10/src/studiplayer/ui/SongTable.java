package studiplayer.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import studiplayer.audio.AudioFile;
import studiplayer.audio.PlayList;
import studiplayer.audio.SampledFile;
import studiplayer.audio.TaggedFile;

public class SongTable extends TableView<Song> {
	private ObservableList<Song> tableData;
	private PlayList playList;
	
	/**
	 * Initialises the table with the data from the PlayList and sets table headers
	 *
	 * @param playList
	 */
	public SongTable(PlayList playList) {
		this.playList = playList;
		this.tableData = FXCollections.observableArrayList();
		setItems(tableData);
		
		TableColumn<Song, String> interpretColumn = new TableColumn<>("Artist");
		interpretColumn.setCellValueFactory(
						new PropertyValueFactory<Song, String>("interpret"));
		interpretColumn.setSortable(false);
		TableColumn<Song, String> titelColumn = new TableColumn<>("Title");
		titelColumn.setCellValueFactory(
						new PropertyValueFactory<Song, String>("titel"));
		titelColumn.setSortable(false);
		TableColumn<Song, String> albumColumn = new TableColumn<>("Album");
		albumColumn.setCellValueFactory(
						new PropertyValueFactory<Song, String>("album"));
		albumColumn.setSortable(false);
		TableColumn<Song, String> laengeColumn = new TableColumn<>("Duration");
		laengeColumn.setCellValueFactory(
						new PropertyValueFactory<Song, String>("laenge"));
		laengeColumn.setSortable(false);
		getColumns().add(interpretColumn);
		getColumns().add(titelColumn);
		getColumns().add(albumColumn);
		getColumns().add(laengeColumn);
		setEditable(false);
		refreshSongs();
	}
	
	/**
	 * Registers an event handler for mouse clicks on a table row
	 *
	 * @param handler
	 */
	public void setRowSelectionHandler(EventHandler<? super MouseEvent> handler) {
		setOnMouseClicked(handler);
	}
	
	/**
	 * Redisplay the table after changes (entries, configuration) to the play list
	 */
	public void refreshSongs() {
		tableData.clear();
		AudioFile first = playList.currentAudioFile();
		/* String firstAlbum = (first instanceof TaggedFile)? ((TaggedFile) first).getAlbum() : "";
		String firstLaenge = (first instanceof SampledFile)? ((SampledFile) first).formatDuration() : "";
		tableData.add(new Song(first, first.getAuthor(), first.getTitle(), firstAlbum, firstLaenge));*/
		
		playList.nextSong();
		AudioFile curr;
		do {
			String album = "";
			String laenge = "";
			
			curr = playList.currentAudioFile();
			
			album = (curr instanceof TaggedFile)? ((TaggedFile) curr).getAlbum() : "";
			laenge = (curr instanceof SampledFile)? ((SampledFile) curr).formatDuration() : "";
			
			tableData.add(new Song(curr, curr.getAuthor(), curr.getTitle(), album, laenge));
			playList.nextSong();
		} while (first != curr);
	}
	
	/**
	 * Selects audiofile "song" in the table
	 *
	 * @param song
	 */
	public void selectSong(AudioFile song) {
		AudioFile currentAudioFile = playList.currentAudioFile();
		int index = 0;
		
		for (Song s : tableData) {
			if (s.getAudioFile() == currentAudioFile) {
				getSelectionModel().select(index);
			}
			++index;
		}
	}
}
