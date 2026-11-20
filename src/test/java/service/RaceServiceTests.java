package service;

import org.junit.jupiter.api.Test;

public class RaceServiceTests {

	// updateCourse
	@Test
	public void given_raceHas3Laps_when_courseUpdatedTo3Laps_then_raceHas3Laps() {
		
	}
	
	@Test
	public void given_raceHas3Laps_when_courseUpdatedTo2Laps_then_raceHas2Laps() {
		
	}
	
	@Test
	public void given_raceHas3LapsAndAnEntryHasSailed2Laps_when_courseUpdatedTo3Laps_then_EntryHasFinished() {
		
	}
	
	@Test
	public void given_raceHas3LapsAndAnEntryHasSailed3Laps_when_courseUpdatedTo2Laps_then_throwsException() {
		
	}
	
	
	// updateProgressIndicators
	@Test
	public void given_raceHas3LapsAndAStandardFinishAndEntryHasSailedNoLaps_when_lapAddedToEntry_then_EntryIsNotOnLastLapAndHasNotFinished() {
		
	}
	
	@Test
	public void given_raceHas3LapsAndAStandardFinishAndEntryHasSailed1Lap_when_lapAddedToEntry_then_EntryIsOnLastLapAndHasNotFinished() {
		
	}
	
	@Test
	public void given_raceHas3LapsAndAStandardFinishAndEntryHasSailed2Laps_when_lapAddedToEntry_then_EntryIsNotOnLastLapAndHasFinished() {
		
	}
	
	@Test
	public void given_raceHas3LapsAndAGrandPrixFinishAndEntryHasSailedNoLaps_when_lapAddedToEntry_then_EntryIsNotOnLastLapAndHasNotFinished() {
	
	}
	
	@Test
	public void given_raceHas3LapsAndAGrandPrixFinishAndEntryHasSailed1Lap_when_lapAddedToEntry_then_EntryIsOnLastLapAndHasNotFinished() {
		
	}
	
	@Test
	public void given_raceHas3LapsAndAGrandPrixFinishAndEntryHasSailed2Laps_when_lapAddedToEntry_then_EntryIsNotOnLastLapAndHasFinished() {
		
	}
	
	@Test
	public void given_raceHas3LapsAndAGrandPrixFinishAndEntryHasSailedNoLaps_when_aDifferentEntryFinishesTheRace_then_EntryIsOnLastLapAndHasNotFinished() {
	
	}
	
	@Test
	public void given_raceHas3LapsAndAGrandPrixFinishAndEntryHasSailedNoLapsAndAnotherEntryHasFinishedRace_when_lapAddedToEntry_then_EntryIsNotOnLastLapAndHasFinished() {
		
	}
}
