package gui.controller

import function.RankListScraper
import gui.view.ScrapeRankListView
import gui.view.StandardLineupView
import io.JsonFileHandler
import javafx.application.Platform
import javafx.scene.control.Alert
import javafx.util.Duration
import model.Player
import tornadofx.*

class ScrapeRankListController : Controller() {
    val scraper = RankListScraper()
    val view: ScrapeRankListView by inject()

    fun scrape() {
        var players: List<Player> = listOf()

        try { // Scrape players from rank list
            players = scraper.scrapeRankList()
        } catch (e: Exception) {
            println("Exception in scraper)")
            println("$e\n")
            println(e.stackTrace.joinToString(separator = "\n"))
        }

        if (players.isNotEmpty()) {
            try {
                println("Saving players in appdata...")
                JsonFileHandler.saveJsonPlayerFile(players)
                runLater(Duration.millis(60.0)) {
                    alert(Alert.AlertType.INFORMATION, "Players updated successfully.\n" +
                            "${players.size} players scraped.")
                }
                println("Player saved successfully!")
            } catch (e: Exception) {
                println("Unable to save players from local JSON")
                Platform.runLater {
                    alert(Alert.AlertType.ERROR, "Unknown error in saving scraped players locally...\n " +
                            "Using previous locally saved players.")
                }
                throw e
            }
        } else {
            println("No players scraped...")
            Platform.runLater {
                alert(Alert.AlertType.INFORMATION, "")
            }
        }

        Platform.runLater {
            view.replaceWith(StandardLineupView::class)
        }
    }
}