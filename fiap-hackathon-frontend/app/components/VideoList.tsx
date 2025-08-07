'use client'

import { useState, useEffect } from 'react'
import { Video, Clock, CheckCircle, AlertCircle, Eye, Download } from 'lucide-react'

interface VideoItem {
  id: string;
  videoName: string;
  videoSize: string;
  tempoDeProcessamento: string;
  message: string;
}

interface VideoListProps {
  onSelectVideo: (video: VideoItem) => void;
  currentVideoId?: string;
}

export default function VideoList({ onSelectVideo, currentVideoId }: VideoListProps) {
  const [videos, setVideos] = useState<VideoItem[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [videoStatuses, setVideoStatuses] = useState<Record<string, string>>({})

  // Carregar lista de vídeos
  useEffect(() => {
    const fetchVideos = async () => {
      try {
        setLoading(true)
        const response = await fetch('/api/videos/list')
        
        if (response.ok) {
          const videoList = await response.json()
          setVideos(videoList)
          
          // Buscar status de cada vídeo
          const statusPromises = videoList.map(async (video: VideoItem) => {
            try {
              const statusResponse = await fetch(`/api/videos/status/${video.id}`)
              if (statusResponse.ok) {
                const status = await statusResponse.json()
                return { id: video.id, status: status.status || 'UNKNOWN' }
              }
              return { id: video.id, status: 'UNKNOWN' }
            } catch {
              return { id: video.id, status: 'ERROR' }
            }
          })
          
          const statuses = await Promise.all(statusPromises)
          const statusMap = statuses.reduce((acc, { id, status }) => {
            acc[id] = status
            return acc
          }, {} as Record<string, string>)
          
          setVideoStatuses(statusMap)
        } else {
          setError('Erro ao carregar lista de vídeos')
        }
      } catch (err) {
        setError('Erro de conexão ao carregar vídeos')
        console.error('Erro ao carregar vídeos:', err)
      } finally {
        setLoading(false)
      }
    }

    fetchVideos()
  }, [])

  // Atualizar status periodicamente
  useEffect(() => {
    const interval = setInterval(async () => {
      const statusPromises = videos.map(async (video) => {
        try {
          const response = await fetch(`/api/videos/status/${video.id}`)
          if (response.ok) {
            const status = await response.json()
            return { id: video.id, status: status.status || 'UNKNOWN' }
          }
          return { id: video.id, status: videoStatuses[video.id] || 'UNKNOWN' }
        } catch {
          return { id: video.id, status: videoStatuses[video.id] || 'ERROR' }
        }
      })
      
      const statuses = await Promise.all(statusPromises)
      const statusMap = statuses.reduce((acc, { id, status }) => {
        acc[id] = status
        return acc
      }, {} as Record<string, string>)
      
      setVideoStatuses(statusMap)
    }, 5000) // Atualiza a cada 5 segundos

    return () => clearInterval(interval)
  }, [videos, videoStatuses])

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'CONCLUIDO':
      case 'COMPLETED':
        return <CheckCircle className="text-green-500" size={16} />
      case 'PROCESSING':
      case 'EM_PROCESSAMENTO':
        return <Clock className="text-blue-500 animate-pulse" size={16} />
      case 'ERROR':
        return <AlertCircle className="text-red-500" size={16} />
      default:
        return <Clock className="text-gray-400" size={16} />
    }
  }

  const getStatusText = (status: string) => {
    switch (status) {
      case 'CONCLUIDO':
      case 'COMPLETED':
        return 'Concluído'
      case 'PROCESSING':
      case 'EM_PROCESSAMENTO':
        return 'Processando...'
      case 'ERROR':
        return 'Erro'
      case 'Uploaded':
        return 'Aguardando'
      default:
        return 'Desconhecido'
    }
  }

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'CONCLUIDO':
      case 'COMPLETED':
        return 'bg-green-50 border-green-200'
      case 'PROCESSING':
      case 'EM_PROCESSAMENTO':
        return 'bg-blue-50 border-blue-200'
      case 'ERROR':
        return 'bg-red-50 border-red-200'
      default:
        return 'bg-gray-50 border-gray-200'
    }
  }

  if (loading) {
    return (
      <div className="bg-white rounded-lg shadow-lg p-6">
        <div className="flex items-center justify-center py-8">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
          <span className="ml-2 text-gray-600">Carregando vídeos...</span>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="bg-white rounded-lg shadow-lg p-6">
        <div className="text-center text-red-600 py-8">
          <AlertCircle size={48} className="mx-auto mb-4" />
          <p>{error}</p>
        </div>
      </div>
    )
  }

  if (videos.length === 0) {
    return (
      <div className="bg-white rounded-lg shadow-lg p-6">
        <div className="text-center text-gray-500 py-8">
          <Video size={48} className="mx-auto mb-4" />
          <p>Nenhum vídeo processado encontrado.</p>
          <p className="text-sm mt-2">Faça upload de um vídeo para começar.</p>
        </div>
      </div>
    )
  }

  return (
    <div className="bg-white rounded-lg shadow-lg p-6">
      <h2 className="text-xl font-semibold text-gray-800 mb-4 flex items-center gap-2">
        <Video size={24} />
        Vídeos Processados ({videos.length})
      </h2>
      
      <div className="space-y-3">
        {videos.map((video) => {
          const status = videoStatuses[video.id] || 'UNKNOWN'
          const isSelected = currentVideoId === video.id
          
          return (
            <div
              key={video.id}
              className={`
                p-4 rounded-lg border transition-all cursor-pointer
                ${isSelected ? 'ring-2 ring-blue-500 border-blue-300' : 'hover:border-gray-300'}
                ${getStatusColor(status)}
              `}
              onClick={() => onSelectVideo(video)}
            >
              <div className="flex items-center justify-between">
                <div className="flex-1">
                  <div className="flex items-center gap-3 mb-2">
                    {getStatusIcon(status)}
                    <h3 className="font-medium text-gray-800 truncate">
                      { `Video ${video.id.substring(0, 8)}`}
                    </h3>
                    <span className="text-xs text-gray-500 bg-white px-2 py-1 rounded">
                      {getStatusText(status)}
                    </span>
                  </div>
                  
                  <div className="grid grid-cols-2 gap-4 text-xs text-gray-600">
                    <div>
                      <span className="font-medium">ID:</span> {video.id.substring(0, 8)}...
                    </div>
                    <div>
                      <span className="font-medium">Frames:</span> {video.videoSize || 'N/A'}
                    </div>
                  </div>
                </div>

                <div className="flex gap-2">
                  {(status === 'CONCLUIDO' || status === 'COMPLETED') && (
                    <button
                      onClick={(e) => {
                        e.stopPropagation()
                        onSelectVideo(video)
                      }}
                      className="p-2 bg-blue-500 text-white rounded-full hover:bg-blue-600 transition-colors"
                      title="Visualizar frames"
                    >
                      <Eye size={16} />
                    </button>
                  )}
                </div>
              </div>
            </div>
          )
        })}
      </div>
    </div>
  )
}
