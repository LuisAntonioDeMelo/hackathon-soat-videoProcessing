'use client'

import { useState, useCallback, useEffect } from 'react'
import { Upload, Video, Image, Download, AlertCircle, CheckCircle, Clock } from 'lucide-react'
import VideoUpload from './components/VideoUpload'
import VideoStatus from './components/VideoStatus'
import FrameViewer from './components/FrameViewer'
import VideoList from './components/VideoList'

interface VideoResponse {
  id: string;
  message: string;
  videoName: string;
  videoSize: string;
  tempoDeProcessamento: string;
}

interface VideoStatusData {
  id: string;
  status: 'PROCESSING' | 'COMPLETED' | 'ERROR' | 'EM_PROCESSAMENTO' | 'CONCLUIDO';
  message?: string;
  frameCount?: number;
  zipPath?: string;
}

interface VideoItem {
  id: string;
  videoName: string;
  videoSize: string;
  tempoDeProcessamento: string;
  message: string;
}

export default function Home() {
  const [selectedVideo, setSelectedVideo] = useState<VideoItem | null>(null)
  const [videoStatus, setVideoStatus] = useState<VideoStatusData | null>(null)
  const [isPolling, setIsPolling] = useState(false)
  const [showUpload, setShowUpload] = useState(false)
  const [refreshKey, setRefreshKey] = useState(0)

  // Poll para verificar status do vídeo selecionado
  useEffect(() => {
    let intervalId: NodeJS.Timeout | null = null;

    if (selectedVideo && isPolling) {
      intervalId = setInterval(async () => {
        try {
          const response = await fetch(`/api/videos/status/${selectedVideo.id}`);
          if (response.ok) {
            const status = await response.json();
            setVideoStatus(status);
            
            if (status.status === 'COMPLETED' || status.status === 'ERROR' || status.status === 'CONCLUIDO') {
              setIsPolling(false);
              setRefreshKey(prev => prev + 1); // Força refresh da lista
            }
          }
        } catch (error) {
          console.error('Erro ao verificar status:', error);
        }
      }, 2000); // Verifica a cada 2 segundos
    }

    return () => {
      if (intervalId) {
        clearInterval(intervalId);
      }
    };
  }, [selectedVideo, isPolling]);

  const handleVideoUploaded = useCallback((videoData: VideoResponse) => {
    const newVideo: VideoItem = {
      id: videoData.id,
      videoName: videoData.videoName,
      videoSize: videoData.videoSize,
      tempoDeProcessamento: videoData.tempoDeProcessamento,
      message: videoData.message
    };
    
    setSelectedVideo(newVideo);
    setVideoStatus({
      id: videoData.id,
      status: 'PROCESSING'
    });
    setIsPolling(true);
    setShowUpload(false);
    setRefreshKey(prev => prev + 1); // Força refresh da lista
  }, []);

  const handleSelectVideo = useCallback(async (video: VideoItem) => {
    setSelectedVideo(video);
    
    // Buscar status atual do vídeo
    try {
      const response = await fetch(`/api/videos/status/${video.id}`);
      if (response.ok) {
        const status = await response.json();
        setVideoStatus(status);
        
        // Se ainda está processando, iniciar polling
        if (status.status === 'PROCESSING' || status.status === 'EM_PROCESSAMENTO') {
          setIsPolling(true);
        }
      }
    } catch (error) {
      console.error('Erro ao buscar status:', error);
    }
  }, []);

  const handleNewUpload = () => {
    setShowUpload(true);
    setSelectedVideo(null);
    setVideoStatus(null);
    setIsPolling(false);
  };

  return (
    <main className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 p-4">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="text-center mb-8">
          <h1 className="text-4xl font-bold text-gray-800 mb-2 flex items-center justify-center gap-3">
            <Video className="text-blue-600" size={36} />
            FIAP Hackathon - Video Processor
          </h1>
          <p className="text-gray-600 text-lg">
            Faça upload de um vídeo e visualize os frames extraídos
          </p>
          
          {/* Botão novo upload */}
          {!showUpload && (
            <button
              onClick={handleNewUpload}
              className="mt-4 px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors flex items-center gap-2 mx-auto"
            >
              <Upload size={20} />
              Novo Upload
            </button>
          )}
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Lista de vídeos - sempre visível */}
          <div className="lg:col-span-1">
            <VideoList 
              onSelectVideo={handleSelectVideo} 
              currentVideoId={selectedVideo?.id}
              key={refreshKey}
            />
          </div>

          {/* Área principal */}
          <div className="lg:col-span-2 space-y-6">
            {showUpload && (
              /* Upload Section */
              <div className="bg-white rounded-lg shadow-lg p-8">
                <div className="flex items-center justify-between mb-6">
                  <h2 className="text-2xl font-semibold text-gray-800">Upload de Vídeo</h2>
                  <button
                    onClick={() => setShowUpload(false)}
                    className="text-gray-400 hover:text-gray-600"
                  >
                    ✕
                  </button>
                </div>
                <VideoUpload onVideoUploaded={handleVideoUploaded} />
              </div>
            )}

            {selectedVideo && (
              <div className="space-y-6">
                {/* Video Info Card */}
                <div className="bg-white rounded-lg shadow-lg p-6">
                  <h2 className="text-2xl font-semibold text-gray-800 mb-4 flex items-center gap-2">
                    <Video size={24} />
                    {selectedVideo.videoName || `Vídeo ${selectedVideo.id.substring(0, 8)}`}
                  </h2>
                  
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm text-gray-600">
                    <div>
                      <span className="font-medium">ID:</span> {selectedVideo.id.substring(0, 8)}...
                    </div>
                    <div>
                      <span className="font-medium">Frames:</span> {selectedVideo.videoSize || 'N/A'}
                    </div>
                  </div>
                </div>

                {/* Status Section */}
                <VideoStatus 
                  videoId={selectedVideo.id} 
                  status={videoStatus} 
                />

                {/* Frame Viewer - só mostra quando processamento completo */}
                {(videoStatus?.status === 'COMPLETED' || videoStatus?.status === 'CONCLUIDO') && (
                  <div className="bg-white rounded-lg shadow-lg p-6">
                    <h2 className="text-2xl font-semibold text-gray-800 mb-4 flex items-center gap-2">
                      <Image size={24} />
                      Frames Extraídos
                    </h2>
                    <FrameViewer 
                      videoId={selectedVideo.id} 
                      frameCount={parseInt(selectedVideo.videoSize) || 0}
                    />
                  </div>
                )}
              </div>
            )}

            {/* Mensagem quando nenhum vídeo selecionado */}
            {!selectedVideo && !showUpload && (
              <div className="bg-white rounded-lg shadow-lg p-12 text-center">
                <Video size={64} className="mx-auto text-gray-300 mb-4" />
                <h3 className="text-xl font-medium text-gray-600 mb-2">
                  Selecione um vídeo
                </h3>
                <p className="text-gray-500">
                  Escolha um vídeo da lista ao lado para visualizar os frames ou faça um novo upload.
                </p>
              </div>
            )}
          </div>
        </div>
      </div>
    </main>
  )
}
