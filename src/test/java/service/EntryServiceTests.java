package service;

import org.junit.jupiter.api.Test;

public class EntryServiceTests {

	//add lap
	@Test
	public void given_entryExistsAndNotSailedLap_when_addLap_then_returnsEntryWithNewLap() {
		
	}
	
	@Test
	public void given_entryExistsAndNotSailedLap_when_addLap_then_signalsBeforeCreateEventForLap() {
		
	}
	
	@Test
	public void given_entryExistsAndNotSailedLap_when_addLap_then_signalsAfterCreateEventForLap() {
		
	}
	
	@Test
	public void given_entryExistsAndNotSailedLap_when_addLap_then_signalsBeforeLinkSaveEventForEntry() {
		
	}
	
	@Test
	public void given_entryExistsAndNotSailedLap_when_addLap_then_signalsAfterLinkSaveEventForEntry() {
		
	}
	
	@Test
	public void given_entryDoesNotExist_when_addLap_then_throwsEntryNotFoundException() {
		
	}
	
	@Test
	public void given_entryExistsAndHasSailedTheLap_when_addLap_then_throwsDuplicateLapException() {
		
	}
	
	@Test
	public void given_moreThanOneEntryInRace_when_lapAdded_then_calculatesPositionsOfEntriesInRace() {
//		Competitor competitor1 = new Competitor("Bob");
//		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
//		Dinghy dinghy1 = new Dinghy("1234", graduate);
//		Fleet fleet = new Fleet("Test Fleet");
//		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 2, RaceType.FLEET, StartType.CSCCLUBSTART, false);
//		race.signUp(competitor1, dinghy1);
//		SignedUp signedUp1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get();
//		Lap lap1 = new Lap(1, Duration.ofMinutes(13));
//		signedUp1.getEntry().addLap(lap1);
//		
//		assertEquals(1, signedUp1.getPosition());
	}
	
	// removeLap
	
	
}
