# marble

MARBLE project contains two parts:

Capture: capture program runs on a Raspberry Pi. It extracts 3D key points from camera images. The code is in CaptureVideo/

Client: client programs runs on an Android Phone. It reads the 3D key points data from BLE broadcast packages. It renders the avatar on the Phone screen. It performs localization and pose estimation so the rendering above camera preview generates an augmented reality experience. The code is in marble_client.

For questions or suggestions. Please file an issue.
