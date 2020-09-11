package gui.view

import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.collections.transformation.SortedList
import javafx.scene.control.TableRow
import javafx.util.Duration
import model.Player
import tornadofx.*
import javax.swing.text.TableView


class ChoosePlayerFragment(players: ObservableList<Player>, predicate: (Player) -> Boolean) : Fragment() {
    override val root = vbox()

    private val selectedPlayerProperty = SimpleObjectProperty<Player>()
    private val selectedPlayer: Player by selectedPlayerProperty
    private var resultPlayer:Player? = null

    init {
        val filteredPlayers = FilteredList(players)
        filteredPlayers.setPredicate(predicate)

        textfield {
            promptText = "Search for name or ID";
            isFocusTraversable = false;

            textProperty().addListener { _, _, newValue ->
                filteredPlayers.setPredicate { myObject ->
                    // Compare name field in object with filter.
                    val lowerCaseFilter: String = newValue.toLowerCase()

                    // Must adhere to the original predicate from parameter
                    if(predicate(myObject)) {
                        // Filter matches name.
                        if (java.lang.String.valueOf(myObject.name).toLowerCase().contains(lowerCaseFilter))
                            return@setPredicate true

                        // Compare badmintonID field in object with filter.
                        if (myObject.badmintonId.toString().contains(lowerCaseFilter))
                            return@setPredicate true
                    }
                    return@setPredicate false
                }
            }

        }
        val sortableData: SortedList<Player> = SortedList<Player>(filteredPlayers)

        tableview(sortableData) {
            sortableData.comparatorProperty().bind(comparatorProperty())
            root.setPrefSize(623.5, 500.0)
            prefHeightProperty().bind(root.heightProperty())
            prefWidthProperty().bind(root.widthProperty())
            readonlyColumn("Name", Player::name)
            readonlyColumn("ID", Player::badmintonId)
            readonlyColumn("Level Points", Player::levelPoints)
            readonlyColumn("Single Points", Player::singlesPoints)
            readonlyColumn("Double Points", Player::doublesPoints)
            readonlyColumn("Mixed Points", Player::mixedPoints)
            bindSelected(selectedPlayerProperty)

            setRowFactory {
                val row: TableRow<Player> = TableRow()
                row.setOnMouseClicked { event ->
                    if (event.clickCount == 1 && !row.isEmpty) {
                        resultPlayer = selectedPlayer
                        runLater(Duration.millis(60.0)) {
                            close()
                        }
                    }
                }
                row
            }
        }

    }

    fun getResult(): Player? {
        return resultPlayer
    }
}