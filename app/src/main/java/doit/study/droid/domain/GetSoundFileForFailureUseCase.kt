package doit.study.droid.domain

import doit.study.droid.data.Outcome
import doit.study.droid.data.local.SoundRepository
import javax.inject.Inject

class GetSoundFileForFailureUseCase @Inject constructor(private val soundRepository: SoundRepository) {
    operator fun invoke (): Outcome<String> {
        return soundRepository.getRandomSoundFileForFailure()
    }
}